package com.rescatta.backend.report.dto;

import com.rescatta.backend.shared.domain.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Body de {@code PATCH /api/v1/admin/reports/{id}/status}. Al pasar a APROBADO, el
 * reporte queda asignado a la organización del administrador que lo aprueba y se abre
 * automáticamente el chat con el ciudadano. Al pasar a RECHAZADO, conviene enviar
 * {@code rejectionReason} para que el ciudadano entienda por qué.
 */
public record ReportStatusUpdateRequest(

        @NotNull(message = "Selecciona el nuevo estado del reporte.")
        ReportStatus status,

        @Size(max = 500)
        String rejectionReason,

        @Size(max = 1000)
        String adminNotes
) {
}
