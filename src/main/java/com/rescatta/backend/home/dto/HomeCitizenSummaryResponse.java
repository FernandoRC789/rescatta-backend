package com.rescatta.backend.home.dto;

import com.rescatta.backend.report.dto.ReportSummaryResponse;

import java.util.List;

/**
 * Todo lo que necesita pintar la pantalla Home Ciudadano en una sola llamada:
 * el conteo de urgencias cercanas (para el badge "2 urgencias a 1km") y la lista de
 * reportes cercanos ya ordenada por distancia.
 */
public record HomeCitizenSummaryResponse(
        int urgentReportsCount,
        List<ReportSummaryResponse> nearbyReports
) {
}
