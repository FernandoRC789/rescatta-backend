package com.rescatta.backend.organization.dto;

import java.time.Instant;

public record OrganizationResponse(
        Long id,
        String name,
        String type,
        boolean verified,
        String description,
        String logoUrl,
        String coverUrl,
        String address,
        String district,
        Double latitude,
        Double longitude,
        String phone,
        String email,
        String website,
        String scheduleText,
        boolean emergency24h,
        Instant createdAt
) {
}
