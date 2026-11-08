package com.rescatta.backend.shared.domain.enums;

/**
 * Ciclo de vida de un reporte:
 * PENDIENTE (recién creado por el ciudadano)
 *   -> APROBADO (una veterinaria/refugio lo revisó y lo tomó como caso válido)
 *      -> EN_PROCESO (ya están yendo a atender/rescatar al animal)
 *         -> RESCATADO (caso cerrado con éxito)
 *   -> RECHAZADO (no procede: duplicado, información insuficiente, etc.)
 */
public enum ReportStatus {
    PENDIENTE,
    APROBADO,
    EN_PROCESO,
    RESCATADO,
    RECHAZADO
}
