package com.rescatta.backend.report.dto;

import com.rescatta.backend.shared.domain.enums.AgeGroup;
import com.rescatta.backend.shared.domain.enums.ReportCondition;
import com.rescatta.backend.shared.domain.enums.Species;
import jakarta.validation.constraints.*;

/**
 * Datos del formulario "Nuevo Reporte" (sin las fotos — esas llegan como
 * {@code List<MultipartFile>} aparte, ver {@code ReportController}).
 */
public record CreateReportRequest(

        @NotNull(message = "Selecciona el tipo de animal (perro, gato u otro).")
        Species species,

        AgeGroup ageGroup,

        /** Opcional: si no se identifica la raza, se deja null ("No identificada"). */
        Long breedId,

        @NotNull(message = "Selecciona el estado del animal.")
        ReportCondition condition,

        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres.")
        String description,

        @NotNull(message = "La ubicación es obligatoria.")
        @DecimalMin(value = "-90.0", message = "Latitud inválida.")
        @DecimalMax(value = "90.0", message = "Latitud inválida.")
        Double latitude,

        @NotNull(message = "La ubicación es obligatoria.")
        @DecimalMin(value = "-180.0", message = "Longitud inválida.")
        @DecimalMax(value = "180.0", message = "Longitud inválida.")
        Double longitude,

        @Size(max = 255)
        String address,

        boolean reporterCanStay
) {
}
