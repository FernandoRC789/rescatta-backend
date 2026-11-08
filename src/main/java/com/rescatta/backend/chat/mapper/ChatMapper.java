package com.rescatta.backend.chat.mapper;

import com.rescatta.backend.chat.domain.Conversation;
import com.rescatta.backend.chat.domain.Message;
import com.rescatta.backend.chat.dto.ConversationResponse;
import com.rescatta.backend.chat.dto.MessageResponse;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class ChatMapper {

    public ConversationResponse toResponse(Conversation c, boolean forOrgViewer) {
        String cover = c.getReport().getPhotos().stream()
                .min(Comparator.comparingInt(p -> p.getPosition()))
                .map(p -> p.getUrl())
                .orElse(null);

        int unread = forOrgViewer ? c.getUnreadByOrg() : c.getUnreadByCitizen();

        return new ConversationResponse(
                c.getId(),
                c.getReport().getId(),
                c.getReport().getSpecies() != null ? c.getReport().getSpecies().name() : null,
                cover,
                c.getOrganization().getId(),
                c.getOrganization().getName(),
                c.getOrganization().getLogoUrl(),
                c.getCitizenUid(),
                c.getStatus(),
                c.getLastMessagePreview(),
                c.getLastMessageAt(),
                unread,
                c.getCreatedAt()
        );
    }

    public MessageResponse toResponse(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getConversation().getId(),
                m.getSenderUid(),
                m.isSenderIsOrg(),
                m.getSenderName(),
                m.getContent(),
                m.getCreatedAt()
        );
    }
}
