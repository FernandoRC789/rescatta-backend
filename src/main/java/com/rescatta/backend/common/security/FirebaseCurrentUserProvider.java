package com.rescatta.backend.common.security;

import com.rescatta.backend.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class FirebaseCurrentUserProvider implements CurrentUserProvider {

    private static final String FIREBASE_UID_ATTRIBUTE = "firebaseUid";

    @Override
    public Optional<String> getCurrentUserUid() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return Optional.empty();
        }

        HttpServletRequest request = attributes.getRequest();

        Object uid = request.getAttribute(FIREBASE_UID_ATTRIBUTE);

        if (uid == null) {
            return Optional.empty();
        }

        String firebaseUid = uid.toString();

        return firebaseUid.isBlank()
                ? Optional.empty()
                : Optional.of(firebaseUid);
    }

    @Override
    public String requireCurrentUserUid() {
        return getCurrentUserUid()
                .orElseThrow(() -> new UnauthorizedException(
                        "Usuario no autenticado."
                ));
    }
}
