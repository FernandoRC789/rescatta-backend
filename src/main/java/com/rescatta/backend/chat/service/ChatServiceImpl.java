package com.rescatta.backend.chat.service;

import com.rescatta.backend.chat.domain.Conversation;
import com.rescatta.backend.chat.domain.Message;
import com.rescatta.backend.chat.dto.ConversationResponse;
import com.rescatta.backend.chat.dto.MessageResponse;
import com.rescatta.backend.chat.mapper.ChatMapper;
import com.rescatta.backend.chat.repository.ConversationRepository;
import com.rescatta.backend.chat.repository.MessageRepository;
import com.rescatta.backend.common.exception.ForbiddenException;
import com.rescatta.backend.common.exception.ResourceNotFoundException;
import com.rescatta.backend.report.domain.AnimalReport;
import com.rescatta.backend.user.domain.AppUser;
import com.rescatta.backend.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private static final int PREVIEW_LENGTH = 120;

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AppUserRepository appUserRepository;
    private final ChatMapper chatMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void ensureConversationForReport(AnimalReport report) {
        if (report.getOrganization() == null) {
            return;
        }
        conversationRepository.findByReportId(report.getId()).orElseGet(() ->
                conversationRepository.save(Conversation.builder()
                        .report(report)
                        .organization(report.getOrganization())
                        .citizenUid(report.getReporterUid())
                        .build())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getConversationForReport(Long reportId, String requesterUid) {
        Conversation conversation = conversationRepository.findByReportId(reportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Este reporte aún no tiene un chat activo (se habilita cuando una organización lo aprueba)."));
        boolean isOrgViewer = assertParticipantAndCheckIsOrg(conversation, requesterUid);
        return chatMapper.toResponse(conversation, isOrgViewer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConversationResponse> listForCitizen(String citizenUid, Pageable pageable) {
        return conversationRepository.findByCitizenUidOrderByLastMessageAtDescCreatedAtDesc(citizenUid, pageable)
                .map(c -> chatMapper.toResponse(c, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConversationResponse> listForOrganization(Long organizationId, Pageable pageable) {
        return conversationRepository.findByOrganizationIdOrderByLastMessageAtDescCreatedAtDesc(organizationId, pageable)
                .map(c -> chatMapper.toResponse(c, true));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(Long conversationId, String requesterUid, Pageable pageable) {
        Conversation conversation = findConversationOrThrow(conversationId);
        assertParticipantAndCheckIsOrg(conversation, requesterUid);
        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(chatMapper::toResponse);
    }

    @Override
    public MessageResponse sendMessage(Long conversationId, String senderUid, String senderName, String content) {
        Conversation conversation = findConversationOrThrow(conversationId);
        boolean isOrgSender = assertParticipantAndCheckIsOrg(conversation, senderUid);

        Message message = messageRepository.save(Message.builder()
                .conversation(conversation)
                .senderUid(senderUid)
                .senderIsOrg(isOrgSender)
                .senderName(senderName)
                .content(content)
                .build());

        conversation.setLastMessageAt(message.getCreatedAt());
        conversation.setLastMessagePreview(truncate(content));
        if (isOrgSender) {
            conversation.setUnreadByCitizen(conversation.getUnreadByCitizen() + 1);
        } else {
            conversation.setUnreadByOrg(conversation.getUnreadByOrg() + 1);
        }
        conversationRepository.save(conversation);

        MessageResponse response = chatMapper.toResponse(message);
        // Empuja el mensaje en tiempo real a quien esté suscrito al hilo (ver ChatWebSocketConfig).
        messagingTemplate.convertAndSend("/topic/conversations/" + conversationId, response);
        return response;
    }

    @Override
    public void markRead(Long conversationId, String requesterUid) {
        Conversation conversation = findConversationOrThrow(conversationId);
        boolean isOrgViewer = assertParticipantAndCheckIsOrg(conversation, requesterUid);
        if (isOrgViewer) {
            conversation.setUnreadByOrg(0);
        } else {
            conversation.setUnreadByCitizen(0);
        }
        conversationRepository.save(conversation);
    }

    // MARK: - Helpers

    private Conversation findConversationOrThrow(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Conversación", id));
    }

    /** Verifica que el uid pertenezca al hilo (ciudadano dueño, o admin de la organización) y dice si es admin. */
    private boolean assertParticipantAndCheckIsOrg(Conversation conversation, String uid) {
        if (conversation.getCitizenUid().equals(uid)) {
            return false;
        }
        AppUser user = appUserRepository.findByUid(uid).orElse(null);
        boolean belongsToOrg = user != null && user.getOrganization() != null
                && user.getOrganization().getId().equals(conversation.getOrganization().getId());
        if (!belongsToOrg) {
            throw new ForbiddenException("No tienes acceso a esta conversación.");
        }
        return true;
    }

    private String truncate(String content) {
        return content.length() <= PREVIEW_LENGTH ? content : content.substring(0, PREVIEW_LENGTH) + "…";
    }
}
