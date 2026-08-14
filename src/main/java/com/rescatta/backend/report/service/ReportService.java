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
}
