package com.rescatta.backend.catalog.mapper;

import com.rescatta.backend.catalog.domain.Pet;
import com.rescatta.backend.catalog.domain.PetPhoto;
import com.rescatta.backend.catalog.dto.PetDetailResponse;
import com.rescatta.backend.catalog.dto.PetSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "breedName", expression = "java(pet.getBreed() != null ? pet.getBreed().getName() : null)")
    @Mapping(target = "organizationName", source = "organization.name")
    @Mapping(target = "organizationVerified", source = "organization.verified")
    @Mapping(target = "photoUrls", expression = "java(toOrderedUrls(pet.getPhotos()))")
    PetDetailResponse toDetail(Pet pet);

    default PetSummaryResponse toSummary(Pet pet) {
        return toSummary(pet, null);
    }

    default PetSummaryResponse toSummary(Pet pet, Double distanceKm) {
        String cover = pet.getPhotos().stream()
                .min(Comparator.comparingInt(PetPhoto::getPosition))
                .map(PetPhoto::getUrl)
                .orElse(null);

        return new PetSummaryResponse(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getSex(),
                pet.getAgeDescription(),
                pet.getSize(),
                pet.getDistrict(),
                pet.getAdoptionStatus(),
                cover,
                distanceKm
        );
    }

    default List<String> toOrderedUrls(List<PetPhoto> photos) {
        return photos.stream()
                .sorted(Comparator.comparingInt(PetPhoto::getPosition))
                .map(PetPhoto::getUrl)
                .toList();
    }
}
