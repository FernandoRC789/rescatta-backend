package com.rescatta.backend.user.controller;

import com.rescatta.backend.common.api.ApiResponse;
import com.rescatta.backend.common.security.AuthContext;
import com.rescatta.backend.user.domain.AppUser;
import com.rescatta.backend.user.dto.AppUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * "¿Quién soy?" — lo primero que llama tanto la app iOS como el panel web justo después
 * de iniciar sesión con Firebase, para saber si es un CIUDADANO o un ADMINISTRADOR (y de
 * qué organización), y así decidir a qué pantalla llevar al usuario.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Resolución de identidad y rol a partir del token de Firebase")
public class AuthController {

    private final AuthContext authContext;

    @GetMapping("/me")
    @Operation(summary = "Devuelve el perfil y rol del usuario autenticado (lo crea si es la primera vez)")
    public ApiResponse<AppUserResponse> me() {
        AppUser user = authContext.currentUser();
        return ApiResponse.ok(toResponse(user));
    }

    private AppUserResponse toResponse(AppUser user) {
        return new AppUserResponse(
                user.getId(),
                user.getUid(),
                user.getEmail(),
                user.getDisplayName(),
                user.getPhone(),
                user.getRole(),
                user.getOrganization() != null ? user.getOrganization().getId() : null,
                user.getOrganization() != null ? user.getOrganization().getName() : null,
                user.getOrganization() != null && user.getOrganization().isVerified()
        );
    }
}
