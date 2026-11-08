package com.rescatta.backend.chat.websocket;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * SockJS no permite enviar headers custom en el handshake HTTP inicial, así que el
 * token de Firebase viaja como query param: {@code /ws?token=<idToken>}. Se verifica acá
 * una sola vez, al abrir la conexión, y el UID queda disponible en la sesión WS.
 */
@Component
public class FirebaseHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) {
        List<String> tokens = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().get("token");
        if (tokens == null || tokens.isEmpty()) {
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(tokens.get(0));
            attributes.put("firebaseUid", decoded.getUid());
            return true;
        } catch (Exception e) {
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Exception exception) {
        // No-op
    }
}
