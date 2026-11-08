package com.rescatta.backend.report.controller;

import com.rescatta.backend.common.api.ApiResponse;
import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.common.security.AuthContext;
import com.rescatta.backend.report.dto.ReportResponse;
import com.rescatta.backend.report.dto.ReportStatsResponse;
import com.rescatta.backend.report.dto.ReportStatusUpdateRequest;
import com.rescatta.backend.report.dto.ReportSummaryResponse;
import com.rescatta.backend.report.service.ReportService;
import com.rescatta.backend.shared.domain.enums.ReportStatus;
import com.rescatta.backend.shared.domain.enums.Species;
import com.rescatta.backend.user.domain.AppUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Panel de gestión de reportes para el rol Administrador (veterinaria/refugio):
 * bandeja de reportes (propios + pendientes por tomar), aprobación/rechazo, y
 * transición del ciclo de vida del caso hasta "Rescatado". Todos los endpoints exigen
 * una cuenta ADMINISTRADOR vinculada a una organización (ver {@code AuthContext}).
 */
@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
@Tag(name = "Admin · Reportes", description = "Bandeja, revisión y aprobación de reportes — rol Administrador (veterinaria/refugio)")
public class AdminReportController {

    private final ReportService reportService;
    private final AuthContext authContext;

    @GetMapping
    @Operation(summary = "Bandeja de reportes: los de mi organización + pendientes sin asignar")
    public ApiResponse<PageResponse<ReportSummaryResponse>> inbox(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) Species species,
            @RequestParam(defaultValue = "false") boolean onlyMine,
            Pageable pageable
    ) {
        AppUser admin = authContext.requireAdminWithOrganization();
        return ApiResponse.ok(reportService.listForAdmin(admin.getOrganization().getId(), status, species, onlyMine, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle completo de un reporte para revisión")
    public ApiResponse<ReportResponse> getById(@PathVariable Long id) {
        authContext.requireAdminWithOrganization();
        return ApiResponse.ok(reportService.getById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Aprueba, rechaza o actualiza el estado de un reporte (toma el caso para mi organización)")
    public ApiResponse<ReportResponse> updateStatus(@PathVariable Long id, @RequestBody @Valid ReportStatusUpdateRequest body) {
        AppUser admin = authContext.requireAdminWithOrganization();
        ReportResponse updated = reportService.updateStatus(
                id, admin.getUid(), admin.getOrganization().getId(),
                body.status(), body.rejectionReason(), body.adminNotes()
        );
        return ApiResponse.ok("Estado del reporte actualizado.", updated);
    }

    @GetMapping("/stats")
    @Operation(summary = "Contadores para el dashboard del administrador (pendientes, en proceso, rescatados...)")
    public ApiResponse<ReportStatsResponse> stats() {
        AppUser admin = authContext.requireAdminWithOrganization();
        return ApiResponse.ok(reportService.getStats(admin.getOrganization().getId()));
    }
}
