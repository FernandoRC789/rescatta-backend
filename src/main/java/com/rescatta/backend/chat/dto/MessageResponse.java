package com.rescatta.backend.chat.dto;

import java.time.Instant;

public record MessageResponse(
        Long id,
        Long conversationId,
        String senderUid,
        boolean senderIsOrg,
        String senderName,
        String content,
        Instant createdAt
) {
}
