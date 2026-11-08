package com.rescatta.backend.common.security;

import com.rescatta.backend.common.exception.ForbiddenException;
import com.rescatta.backend.common.exception.UnauthorizedException;
import com.rescatta.backend.shared.domain.enums.UserRole;
import com.rescatta.backend.user.domain.AppUser;
import com.rescatta.backend.user.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Punto único al que los controllers preguntan "¿quién soy y qué puedo hacer?". Resuelve
 * el UID del token de Firebase (via {@link CurrentUserProvider}) y lo convierte en un
 * {@link AppUser} con rol, auto-provisionando el registro si es la primera vez que ese
 * UID llega al backend (tanto para ciudadanos de la app iOS como para administradores
 * del panel web — ambos pasan por el mismo filtro de Firebase).
 */
@Component
@RequiredArgsConstructor
public class AuthContext {

    private final CurrentUserProvider currentUserProvider;
    private final AppUserService appUserService;

    /** Usuario actual, auto-provisionado como CIUDADANO si es nuevo. Lanza 401 si no hay token. */
    public AppUser currentUser() {
        String uid = currentUserProvider.requireCurrentUserUid();
        HttpServletRequest request = currentRequest();
        String email = request == null ? null : asString(request.getAttribute("firebaseEmail"));
        String name = request == null ? null : asString(request.getAttribute("firebaseName"));
        return appUserService.getOrProvision(uid, email, name);
    }

    /** Igual que {@link #currentUser()} pero exige rol ADMINISTRADOR o SUPERADMIN (403 si no). */
    public AppUser requireAdmin() {
        AppUser user = currentUser();
        if (!user.isAdmin()) {
            throw new ForbiddenException("Esta acción requiere una cuenta de veterinaria/refugio.");
        }
        return user;
    }

    /** Igual que {@link #requireAdmin()}, pero además exige que ya tenga una organización vinculada. */
    public AppUser requireAdminWithOrganization() {
        AppUser user = requireAdmin();
        if (user.getOrganization() == null) {
            throw new ForbiddenException("Tu cuenta administradora aún no está vinculada a una organización.");
        }
        return user;
    }

    public AppUser requireSuperAdmin() {
        AppUser user = currentUser();
        if (user.getRole() != UserRole.SUPERADMIN) {
            throw new ForbiddenException("Esta acción requiere privilegios de Rescatta.");
        }
        return user;
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }
}
