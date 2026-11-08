package com.rescatta.backend.chat.controller;

import com.rescatta.backend.common.api.ApiResponse;
import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.common.security.AuthContext;
import com.rescatta.backend.chat.dto.ConversationResponse;
import com.rescatta.backend.chat.dto.MessageResponse;
import com.rescatta.backend.chat.dto.SendMessageRequest;
import com.rescatta.backend.chat.service.ChatService;
import com.rescatta.backend.user.domain.AppUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Chat entre el Ciudadano que reportó un animal y la Organización (veterinaria/refugio)
 * que tomó su caso. Se habilita automáticamente cuando el reporte pasa a APROBADO
 * (ver {@code ReportServiceImpl#updateStatus}). El envío es por REST; los mensajes
 * nuevos también se emiten por WebSocket en {@code /topic/conversations/{id}}
 * (ver {@code WebSocketConfig}) para actualizarse en tiempo real sin hacer polling.
 */
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Conversaciones entre ciudadano y organización sobre un reporte")
public class ChatController {

    private final ChatService chatService;
    private final AuthContext authContext;

    @GetMapping
    @Operation(summary = "Lista mis conversaciones (como ciudadano o como administrador de mi organización)")
    public ApiResponse<PageResponse<ConversationResponse>> myConversations(Pageable pageable) {
        AppUser user = authContext.currentUser();
        var page = user.isAdmin() && user.getOrganization() != null
                ? chatService.listForOrganization(user.getOrganization().getId(), pageable)
                : chatService.listForCitizen(user.getUid(), pageable);
        return ApiResponse.ok(PageResponse.of(page));
    }

    @GetMapping("/report/{reportId}")
    @Operation(summary = "Obtiene (u obtiene el estado de) el chat asociado a un reporte")
    public ApiResponse<ConversationResponse> getByReport(@PathVariable Long reportId) {
        AppUser user = authContext.currentUser();
        return ApiResponse.ok(chatService.getConversationForReport(reportId, user.getUid()));
    }

    @GetMapping("/{conversationId}/messages")
    @Operation(summary = "Historial de mensajes de una conversación (paginado, más recientes primero)")
    public ApiResponse<PageResponse<MessageResponse>> messages(@PathVariable Long conversationId, Pageable pageable) {
        AppUser user = authContext.currentUser();
        return ApiResponse.ok(PageResponse.of(chatService.getMessages(conversationId, user.getUid(), pageable)));
    }

    @PostMapping("/{conversationId}/messages")
    @Operation(summary = "Envía un mensaje en la conversación")
    public ApiResponse<MessageResponse> send(@PathVariable Long conversationId, @RequestBody @Valid SendMessageRequest body) {
        AppUser user = authContext.currentUser();
        MessageResponse sent = chatService.sendMessage(conversationId, user.getUid(), user.getDisplayName(), body.content());
        return ApiResponse.ok(sent);
    }

    @PostMapping("/{conversationId}/read")
    @Operation(summary = "Marca la conversación como leída para el usuario actual")
    public ApiResponse<Void> markRead(@PathVariable Long conversationId) {
        AppUser user = authContext.currentUser();
        chatService.markRead(conversationId, user.getUid());
        return ApiResponse.ok(null);
    }
}
