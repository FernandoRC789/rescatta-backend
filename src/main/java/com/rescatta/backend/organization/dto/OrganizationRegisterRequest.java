package com.rescatta.backend.organization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos del formulario "Registrar mi veterinaria/refugio" (panel web). Quien envía esta
 * request queda vinculado como el primer ADMINISTRADOR de la organización creada; el
 * campo {@code verified} arranca en false hasta que Rescatta la valide.
 */
public record OrganizationRegisterRequest(

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 120)
        String name,

        @NotBlank(message = "Indica si eres veterinaria o refugio.")
        String type,

        @Size(max = 1000)
        String description,

        @NotBlank(message = "La dirección es obligatoria.")
        @Size(max = 255)
        String address,

        String district,

        Double latitude,

        Double longitude,

        @NotBlank(message = "El teléfono de contacto es obligatorio.")
        String phone,

        @Email(message = "Email inválido.")
        String email,

        String website,

        String scheduleText,

        boolean emergency24h
) {
}
