package com.rescatta.backend.report.dto;

public record ReportStatsResponse(
        long pendientes,
        long aprobados,
        long enProceso,
        long rescatados,
        long rechazados,
        long total
) {
}
