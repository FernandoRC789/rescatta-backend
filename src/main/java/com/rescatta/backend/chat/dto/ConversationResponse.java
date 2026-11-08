package com.rescatta.backend.chat.dto;

import com.rescatta.backend.shared.domain.enums.ConversationStatus;

import java.time.Instant;

public record ConversationResponse(
        Long id,
        Long reportId,
        String reportSpecies,
        String reportCoverPhotoUrl,
        Long organizationId,
        String organizationName,
        String organizationLogoUrl,
        String citizenUid,
        ConversationStatus status,
        String lastMessagePreview,
        Instant lastMessageAt,
        int unreadCount,
        Instant createdAt
) {
}
