package com.rescatta.backend.breed.service;

import com.rescatta.backend.breed.domain.Breed;
import com.rescatta.backend.breed.dto.BreedResponse;
import com.rescatta.backend.breed.repository.BreedRepository;
import com.rescatta.backend.shared.domain.enums.Species;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BreedServiceImpl implements BreedService {

    private final BreedRepository breedRepository;

    @Override
    public List<BreedResponse> listBySpecies(Species species) {
        return breedRepository.findBySpeciesOrderByNameAsc(species).stream()
                .map(this::toResponse)
                .toList();
    }

    private BreedResponse toResponse(Breed breed) {
        return new BreedResponse(breed.getId(), breed.getName(), breed.getSpecies());
    }
}
