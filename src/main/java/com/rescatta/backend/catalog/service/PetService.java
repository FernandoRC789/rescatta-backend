package com.rescatta.backend.catalog.service;

import com.rescatta.backend.catalog.dto.PetDetailResponse;
import com.rescatta.backend.catalog.dto.PetSummaryResponse;
import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.shared.domain.enums.AdoptionStatus;
import com.rescatta.backend.shared.domain.enums.AgeGroup;
import com.rescatta.backend.shared.domain.enums.Size;
import com.rescatta.backend.shared.domain.enums.Species;
import org.springframework.data.domain.Pageable;

public interface PetService {

    PetDetailResponse getById(Long id);

    /**
     * Búsqueda del catálogo con filtros opcionales. Si {@code latitude}/{@code longitude}
     * llegan, el resultado se ordena por cercanía y se incluye {@code distanceKm} en cada
     * ítem; si no, se ordena por fecha de publicación (más recientes primero).
     */
    PageResponse<PetSummaryResponse> search(
            Species species,
            AgeGroup ageGroup,
            Size size,
            AdoptionStatus adoptionStatus,
            Double latitude,
            Double longitude,
            Double radiusKm,
            Pageable pageable
    );
}
