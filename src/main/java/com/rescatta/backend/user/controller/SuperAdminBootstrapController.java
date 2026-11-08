package com.rescatta.backend.user.controller;

import com.rescatta.backend.common.api.ApiResponse;
import com.rescatta.backend.common.exception.ForbiddenException;
import com.rescatta.backend.common.security.CurrentUserProvider;
import com.rescatta.backend.shared.domain.enums.UserRole;
import com.rescatta.backend.user.domain.AppUser;
import com.rescatta.backend.user.repository.AppUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Convierte al usuario autenticado en SUPERADMIN (staff de Rescatta que verifica
 * organizaciones). Solo funciona si el {@code secret} coincide con la variable de
 * entorno {@code RESCATTA_SUPERADMIN_SECRET} — pensado para usarse una única vez tras
 * el primer deploy (definir la variable, llamar este endpoint autenticado con tu propia
 * cuenta, y luego eliminar/rotar la variable de entorno).
 */
@RestController
@RequestMapping("/api/v1/superadmin")
@RequiredArgsConstructor
@Tag(name = "Rescatta Admin", description = "Bootstrap del primer SUPERADMIN")
public class SuperAdminBootstrapController {

    private final CurrentUserProvider currentUserProvider;
    private final AppUserRepository appUserRepository;
    private final HttpServletRequest request;

    @Value("${rescatta.superadmin.bootstrap-secret:}")
    private String bootstrapSecret;

    @PostMapping("/bootstrap")
    @Operation(summary = "Otorga el rol SUPERADMIN al usuario autenticado (requiere el secret de configuración)")
    public ApiResponse<String> bootstrap(@RequestParam String secret) {
        if (bootstrapSecret == null || bootstrapSecret.isBlank() || !bootstrapSecret.equals(secret)) {
            throw new ForbiddenException("Secret inválido.");
        }
        String uid = currentUserProvider.requireCurrentUserUid();
        AppUser user = appUserRepository.findByUid(uid).orElseGet(() -> AppUser.builder().uid(uid).role(UserRole.CIUDADANO).build());
        user.setRole(UserRole.SUPERADMIN);
        appUserRepository.save(user);
        return ApiResponse.ok("Ahora eres SUPERADMIN.", uid);
    }
}
