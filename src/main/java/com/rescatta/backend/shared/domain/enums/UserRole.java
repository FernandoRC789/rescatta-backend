package com.rescatta.backend.shared.domain.enums;

/**
 * Rol del usuario dentro de la plataforma:
 *  - CIUDADANO: usuario de la app iOS que reporta animales (rol por defecto).
 *  - ADMINISTRADOR: veterinaria/refugio que gestiona reportes desde el panel web.
 *  - SUPERADMIN: staff de Rescatta que verifica/aprueba nuevas veterinarias/refugios.
 */
public enum UserRole {
    CIUDADANO,
    ADMINISTRADOR,
    SUPERADMIN
}
