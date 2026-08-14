package com.rescatta.backend.breed.controller;

import com.rescatta.backend.breed.dto.BreedResponse;
import com.rescatta.backend.breed.service.BreedService;
import com.rescatta.backend.common.api.ApiResponse;
import com.rescatta.backend.shared.domain.enums.Species;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Catálogo de razas — alimenta el desplegable "Raza" del formulario de reporte y de la
 * ficha de mascota. Separado de {@code report}/{@code catalog} porque ambos módulos lo
 * consumen por igual.
 */
@RestController
@RequestMapping("/api/v1/breeds")
@RequiredArgsConstructor
@Tag(name = "Razas", description = "Catálogo de razas por especie (dropdown de formularios)")
public class BreedController {

    private final BreedService breedService;

    @GetMapping
    @Operation(summary = "Lista las razas disponibles para una especie")
    public ApiResponse<List<BreedResponse>> listBySpecies(@RequestParam Species species) {
        return ApiResponse.ok(breedService.listBySpecies(species));
    }
}
