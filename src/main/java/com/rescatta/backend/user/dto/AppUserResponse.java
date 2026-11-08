package com.rescatta.backend.user.dto;

import com.rescatta.backend.shared.domain.enums.UserRole;

public record AppUserResponse(
        Long id,
        String uid,
        String email,
        String displayName,
        String phone,
        UserRole role,
        Long organizationId,
        String organizationName,
        boolean organizationVerified
) {
}
