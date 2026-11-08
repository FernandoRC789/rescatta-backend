package com.rescatta.backend.chat.service;

import com.rescatta.backend.chat.dto.ConversationResponse;
import com.rescatta.backend.chat.dto.MessageResponse;
import com.rescatta.backend.report.domain.AnimalReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {

    /** Crea (si no existe) el hilo de chat de un reporte recién aprobado/asignado. */
    void ensureConversationForReport(AnimalReport report);

    ConversationResponse getConversationForReport(Long reportId, String requesterUid);

    Page<ConversationResponse> listForCitizen(String citizenUid, Pageable pageable);

    Page<ConversationResponse> listForOrganization(Long organizationId, Pageable pageable);

    Page<MessageResponse> getMessages(Long conversationId, String requesterUid, Pageable pageable);

    MessageResponse sendMessage(Long conversationId, String senderUid, String senderName, String content);

    void markRead(Long conversationId, String requesterUid);
}
