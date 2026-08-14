package com.rescatta.backend.catalog.dto;

import com.rescatta.backend.shared.domain.enums.AdoptionStatus;
import com.rescatta.backend.shared.domain.enums.Sex;
import com.rescatta.backend.shared.domain.enums.Size;
import com.rescatta.backend.shared.domain.enums.Species;

/** Versión resumida de una mascota — lo que se muestra en el grid del catálogo (CollectionView). */
public record PetSummaryResponse(
        Long id,
        String name,
        Species species,
        Sex sex,
        String ageDescription,
        Size size,
        String district,
        AdoptionStatus adoptionStatus,
        String coverPhotoUrl,
        Double distanceKm
) {
}
