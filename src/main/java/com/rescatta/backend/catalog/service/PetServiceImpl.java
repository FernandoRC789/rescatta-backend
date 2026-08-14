package com.rescatta.backend.catalog.service;

import com.rescatta.backend.catalog.domain.Pet;
import com.rescatta.backend.catalog.dto.PetDetailResponse;
import com.rescatta.backend.catalog.dto.PetSummaryResponse;
import com.rescatta.backend.catalog.mapper.PetMapper;
import com.rescatta.backend.catalog.repository.PetRepository;
import com.rescatta.backend.catalog.repository.PetSpecifications;
import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.common.exception.ResourceNotFoundException;
import com.rescatta.backend.common.util.GeoUtils;
import com.rescatta.backend.shared.domain.enums.AdoptionStatus;
import com.rescatta.backend.shared.domain.enums.AgeGroup;
import com.rescatta.backend.shared.domain.enums.Size;
import com.rescatta.backend.shared.domain.enums.Species;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;

    @Override
    public PetDetailResponse getById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Mascota", id));
        return petMapper.toDetail(pet);
    }

    @Override
    public PageResponse<PetSummaryResponse> search(
            Species species,
            AgeGroup ageGroup,
            Size size,
            AdoptionStatus adoptionStatus,
            Double latitude,
            Double longitude,
            Double radiusKm,
            Pageable pageable
    ) {
        Specification<Pet> spec = Specification
                .where(PetSpecifications.hasSpecies(species))
                .and(PetSpecifications.hasAgeGroup(ageGroup))
                .and(PetSpecifications.hasSize(size))
                .and(adoptionStatus != null
                        ? PetSpecifications.hasAdoptionStatus(adoptionStatus)
                        : PetSpecifications.excludeAdopted());

        List<Pet> matches = petRepository.findAll(spec);

        boolean filterByLocation = latitude != null && longitude != null;
        double effectiveRadius = radiusKm != null ? radiusKm : Double.MAX_VALUE;

        List<PetSummaryResponse> results;
        if (filterByLocation) {
            results = matches.stream()
                    .filter(pet -> pet.getLatitude() != null && pet.getLongitude() != null)
                    .map(pet -> new PetDistance(pet,
                            GeoUtils.distanceKm(latitude, longitude, pet.getLatitude(), pet.getLongitude())))
                    .filter(pair -> pair.distanceKm() <= effectiveRadius)
                    .sorted(Comparator.comparingDouble(PetDistance::distanceKm))
                    .map(pair -> petMapper.toSummary(pair.pet(), roundTo1Decimal(pair.distanceKm())))
                    .toList();
        } else {
            results = matches.stream()
                    .sorted(Comparator.comparing(Pet::getCreatedAt).reversed())
                    .map(petMapper::toSummary)
                    .toList();
        }

        Page<PetSummaryResponse> page = paginate(results, pageable);
        return PageResponse.ofMapped(page, page.getContent());
    }

    /** Pagina en memoria una lista ya filtrada/ordenada. Ver nota de arquitectura en {@link GeoUtils}. */
    private <T> Page<T> paginate(List<T> fullList, Pageable pageable) {
        int start = (int) pageable.getOffset();
        if (start >= fullList.size()) {
            return new PageImpl<>(List.of(), pageable, fullList.size());
        }
        int end = Math.min(start + pageable.getPageSize(), fullList.size());
        return new PageImpl<>(fullList.subList(start, end), pageable, fullList.size());
    }

    private double roundTo1Decimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private record PetDistance(Pet pet, double distanceKm) {
    }
}
