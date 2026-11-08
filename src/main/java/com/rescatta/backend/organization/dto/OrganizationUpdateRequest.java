package com.rescatta.backend.organization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizationUpdateRequest(

        @NotBlank @Size(max = 120)
        String name,

        String type,

        @Size(max = 1000)
        String description,

        @NotBlank @Size(max = 255)
        String address,

        String district,

        Double latitude,

        Double longitude,

        String phone,

        @Email
        String email,

        String website,

        String scheduleText,

        boolean emergency24h
) {
}
