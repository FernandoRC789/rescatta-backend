package com.rescatta.backend.report.service;

import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.report.dto.CreateReportRequest;
import com.rescatta.backend.report.dto.ReportResponse;
import com.rescatta.backend.report.dto.ReportSummaryResponse;
import com.rescatta.backend.shared.domain.enums.ReportStatus;
import com.rescatta.backend.shared.domain.enums.Species;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService {

    ReportResponse createReport(CreateReportRequest request, List<MultipartFile> photos, String reporterUid);

    ReportResponse getById(Long id);

    PageResponse<ReportSummaryResponse> list(Species species, ReportStatus status, Pageable pageable);

    /** Reportes activos (no rescatados) ordenados por cercanía — para "Reportes cercanos" del Home. */
    List<ReportSummaryResponse> listNearby(double latitude, double longitude, double radiusKm, int limit);

    long countByReporter(String reporterUid);

    PageResponse<ReportSummaryResponse> listMine(String reporterUid, Pageable pageable);

    // MARK: - Panel de Administrador (veterinaria/refugio)

    /**
     * Bandeja del administrador: si {@code onlyMine} es true, solo los reportes ya
     * asignados a su organización; si es false, incluye también los PENDIENTE sin asignar
     * (para poder tomarlos).
     */
    PageResponse<ReportSummaryResponse> listForAdmin(Long organizationId, ReportStatus status, Species species, boolean onlyMine, Pageable pageable);

    ReportResponse updateStatus(Long reportId, String adminUid, Long organizationId,
                                 ReportStatus newStatus, String rejectionReason, String adminNotes);

    com.rescatta.backend.report.dto.ReportStatsResponse getStats(Long organizationId);
}
