package com.rescatta.backend.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Chat en tiempo real: el panel web y (a futuro) la app iOS se conectan a {@code /ws}
 * (STOMP sobre SockJS) y se suscriben a {@code /topic/conversations/{id}} para recibir
 * mensajes nuevos apenas se publican — ver {@code ChatServiceImpl#sendMessage}, que hace
 * el {@code convertAndSend} tras guardar cada mensaje enviado por REST.
 *
 * El envío de mensajes sigue siendo por REST ({@code ChatController#sendMessage}) porque
 * así reutiliza toda la validación/autorización HTTP existente (Firebase filter,
 * AuthContext) sin duplicarla en el canal STOMP.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final FirebaseHandshakeInterceptor firebaseHandshakeInterceptor;

    @org.springframework.beans.factory.annotation.Value("${rescatta.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(allowedOrigins.split(","))
                .addInterceptors(firebaseHandshakeInterceptor)
                .withSockJS();
    }
}
