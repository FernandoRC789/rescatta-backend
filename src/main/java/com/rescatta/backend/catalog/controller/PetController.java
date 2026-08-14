package com.rescatta.backend.catalog.controller;

import com.rescatta.backend.catalog.dto.PetDetailResponse;
import com.rescatta.backend.catalog.dto.PetSummaryResponse;
import com.rescatta.backend.catalog.service.PetService;
import com.rescatta.backend.common.api.ApiResponse;
import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.shared.domain.enums.AdoptionStatus;
import com.rescatta.backend.shared.domain.enums.AgeGroup;
import com.rescatta.backend.shared.domain.enums.Size;
import com.rescatta.backend.shared.domain.enums.Species;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Catálogo de adopción — pantallas "Catálogo de Adopción" y "Ficha de Animal".
 *
 * Los filtros de la pantalla de catálogo (chips "Perro" / "Gato" / "Cachorro" / "Cerca de
 * mí") se resuelven así:
 *  - "Perro" / "Gato"  → {@code species}
 *  - "Cachorro"        → {@code ageGroup=CACHORRO} (independiente de la especie)
 *  - "Cerca de mí"     → se envían {@code latitude}/{@code longitude}; el resultado se
 *                        reordena por cercanía y cada ítem trae su {@code distanceKm}
 *  - "Sano" / "En trámite" (estado) → {@code adoptionStatus}
 */
@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
@Tag(name = "Catálogo de Adopción", description = "Búsqueda y ficha de mascotas listas para adopción")
public class PetController {

    private final PetService petService;

    @GetMapping
    @Operation(summary = "Busca mascotas en el catálogo con filtros opcionales")
    public ApiResponse<PageResponse<PetSummaryResponse>> search(
            @RequestParam(required = false) Species species,
            @RequestParam(required = false) AgeGroup ageGroup,
            @RequestParam(required = false) Size size,
            @RequestParam(required = false) AdoptionStatus adoptionStatus,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radiusKm,
            Pageable pageable
    ) {
        return ApiResponse.ok(petService.search(
                species, ageGroup, size, adoptionStatus, latitude, longitude, radiusKm, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene la ficha completa de una mascota")
    public ApiResponse<PetDetailResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(petService.getById(id));
    }
}
