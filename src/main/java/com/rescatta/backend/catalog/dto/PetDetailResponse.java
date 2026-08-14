package com.rescatta.backend.catalog.dto;

import com.rescatta.backend.shared.domain.enums.*;

import java.util.List;
import java.util.Set;

/** Ficha completa de la mascota — pantalla "Ficha de Animal" / detalle del catálogo. */
public record PetDetailResponse(
        Long id,
        String name,
        Species species,
        String breedName,
        Sex sex,
        AgeGroup ageGroup,
        String ageDescription,
        Size size,
        Double weightKg,
        AdoptionStatus adoptionStatus,
        boolean vaccinated,
        boolean sterilized,
        boolean dewormed,
        Set<TemperamentTag> temperamentTags,
        String healthDescription,
        String description,
        String organizationName,
        boolean organizationVerified,
        String district,
        String adoptionFeeText,
        List<String> photoUrls
) {
}
