package com.rescatta.backend.common.security;

import com.rescatta.backend.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * TODO (siguiente entrega de backend): reemplazar esta implementación por una que
 * verifique el ID Token de Firebase con el Admin SDK:
 *
 * <pre>
 *   FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
 *   String uid = decoded.getUid();
 * </pre>
 *
 * y extraerlo del header {@code Authorization: Bearer <idToken>} mediante un
 * {@code OncePerRequestFilter}. Por ahora, y solo para poder avanzar el resto de la API
 * sin bloquear el desarrollo, se lee un header simple {@code X-User-Uid} que la app
 * enviará temporalmente con el UID que ya le entrega Firebase Auth en el cliente.
 *
 * Ningún controller ni service depende de este detalle — todos dependen de
 * {@link CurrentUserProvider}, así que el día que se reemplace esta clase, no hay que
 * tocar nada más.
 */
//@Component
public class HeaderBasedCurrentUserProvider implements CurrentUserProvider {

    private static final String USER_UID_HEADER = "X-User-Uid";

    @Override
    public Optional<String> getCurrentUserUid() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Optional.empty();
        }
        HttpServletRequest request = attributes.getRequest();
        String uid = request.getHeader(USER_UID_HEADER);
        return (uid == null || uid.isBlank()) ? Optional.empty() : Optional.of(uid);
    }

    @Override
    public String requireCurrentUserUid() {
        return getCurrentUserUid()
                .orElseThrow(() -> new UnauthorizedException(
                        "Falta el header " + USER_UID_HEADER + " (temporal, hasta integrar Firebase Admin SDK)."));
    }
}
