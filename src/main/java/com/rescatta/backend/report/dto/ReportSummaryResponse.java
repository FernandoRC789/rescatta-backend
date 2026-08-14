package com.rescatta.backend.report.dto;

import com.rescatta.backend.shared.domain.enums.ReportCondition;
import com.rescatta.backend.shared.domain.enums.ReportStatus;
import com.rescatta.backend.shared.domain.enums.Species;

import java.time.Instant;

/**
 * Versión resumida de un reporte, pensada para listas (Home Ciudadano → "Reportes
 * cercanos", Dashboard del Rescatista). Trae solo la portada, no las 5 fotos.
 */
public record ReportSummaryResponse(
        Long id,
        Species species,
        ReportCondition condition,
        ReportStatus status,
        String address,
        String coverPhotoUrl,
        Double distanceKm,
        Instant createdAt
) {
}
