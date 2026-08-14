package com.rescatta.backend.report.controller;

import com.rescatta.backend.common.api.ApiResponse;
import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.common.security.CurrentUserProvider;
import com.rescatta.backend.report.dto.CreateReportRequest;
import com.rescatta.backend.report.dto.ReportResponse;
import com.rescatta.backend.report.dto.ReportSummaryResponse;
import com.rescatta.backend.report.service.ReportService;
import com.rescatta.backend.shared.domain.enums.ReportStatus;
import com.rescatta.backend.shared.domain.enums.Species;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Endpoints del flujo "Reportar animal en la calle" (rol Ciudadano).
 *
 * La creación es {@code multipart/form-data} con dos partes:
 *  - {@code report}: JSON con los datos del formulario ({@link CreateReportRequest})
 *  - {@code photos}: 1 a 5 archivos de imagen
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Reporte de animales callejeros: creación, consulta y cercanía")
public class ReportController {

    private final ReportService reportService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un nuevo reporte de animal en la calle, con hasta 5 fotos")
    public ApiResponse<ReportResponse> createReport(
            @RequestPart("report") @Valid CreateReportRequest request,
            @RequestPart("photos") List<MultipartFile> photos
    ) {
        String reporterUid = currentUserProvider.requireCurrentUserUid();
        ReportResponse created = reportService.createReport(request, photos, reporterUid);
        return ApiResponse.ok("Reporte enviado. ¡Gracias por ayudar!", created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene el detalle completo de un reporte")
    public ApiResponse<ReportResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(reportService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Lista reportes con filtros opcionales de especie y estado")
    public ApiResponse<PageResponse<ReportSummaryResponse>> list(
            @RequestParam(required = false) Species species,
            @RequestParam(required = false) ReportStatus status,
            Pageable pageable
    ) {
        return ApiResponse.ok(reportService.list(species, status, pageable));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Reportes activos más cercanos a una ubicación (Home Ciudadano)")
    public ApiResponse<List<ReportSummaryResponse>> listNearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5.0") double radiusKm,
            @Parameter(description = "Máximo de resultados a devolver") @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.ok(reportService.listNearby(latitude, longitude, radiusKm, limit));
    }

    @GetMapping("/mine/count")
    @Operation(summary = "Cantidad de reportes hechos por el usuario autenticado")
    public ApiResponse<Long> countMyReports() {
        String reporterUid = currentUserProvider.requireCurrentUserUid();
        return ApiResponse.ok(reportService.countByReporter(reporterUid));
    }
}
