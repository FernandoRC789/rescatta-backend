package com.rescatta.backend.home.controller;

import com.rescatta.backend.common.api.ApiResponse;
import com.rescatta.backend.home.dto.HomeCitizenSummaryResponse;
import com.rescatta.backend.home.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Datos agregados para la pantalla de inicio de cada rol. Por ahora solo existe la
 * variante Ciudadano; el resumen del Rescatista (contadores del dashboard) se agrega en
 * la siguiente entrega junto al resto del módulo de gestión.
 */
@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "Home", description = "Datos agregados para las pantallas de inicio")
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/citizen-summary")
    @Operation(summary = "Resumen del Home Ciudadano: urgencias y reportes cercanos")
    public ApiResponse<HomeCitizenSummaryResponse> getCitizenSummary(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        return ApiResponse.ok(homeService.getCitizenSummary(latitude, longitude));
    }
}
