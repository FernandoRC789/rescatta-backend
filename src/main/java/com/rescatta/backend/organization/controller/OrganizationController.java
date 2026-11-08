package com.rescatta.backend.organization.controller;

import com.rescatta.backend.common.api.ApiResponse;
import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.common.security.AuthContext;
import com.rescatta.backend.common.security.CurrentUserProvider;
import com.rescatta.backend.organization.dto.OrganizationRegisterRequest;
import com.rescatta.backend.organization.dto.OrganizationResponse;
import com.rescatta.backend.organization.dto.OrganizationUpdateRequest;
import com.rescatta.backend.organization.service.OrganizationService;
import com.rescatta.backend.user.domain.AppUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Perfil de veterinaria/refugio: registro público (rol Administrador), directorio
 * público (consumido por la app iOS: "publicado por Refugio Esperanza ✔", mapa de
 * refugios cercanos), y gestión del propio perfil desde el panel web.
 */
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organizaciones", description = "Veterinarias y refugios: directorio público y perfil de administrador")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final AuthContext authContext;
    private final CurrentUserProvider currentUserProvider;
    private final HttpServletRequest request;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra una nueva veterinaria/refugio y vincula al usuario actual como su administrador")
    public ApiResponse<OrganizationResponse> register(@RequestBody @Valid OrganizationRegisterRequest body) {
        String uid = currentUserProvider.requireCurrentUserUid();
        String email = asString(request.getAttribute("firebaseEmail"));
        String name = asString(request.getAttribute("firebaseName"));
        OrganizationResponse created = organizationService.register(body, uid, email, name);
        return ApiResponse.ok("Organización registrada. Un miembro del equipo Rescatta la validará pronto.", created);
    }

    @GetMapping
    @Operation(summary = "Lista el directorio público de veterinarias/refugios verificados")
    public ApiResponse<PageResponse<OrganizationResponse>> listVerified(
            @RequestParam(required = false) String district,
            Pageable pageable
    ) {
        return ApiResponse.ok(organizationService.listVerified(district, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Perfil público de una veterinaria/refugio")
    public ApiResponse<OrganizationResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(organizationService.getById(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Perfil de la organización del administrador autenticado")
    public ApiResponse<OrganizationResponse> getMine() {
        AppUser admin = authContext.requireAdminWithOrganization();
        return ApiResponse.ok(organizationService.getMine(admin.getUid()));
    }

    @PutMapping("/me")
    @Operation(summary = "Actualiza el perfil de la organización del administrador autenticado")
    public ApiResponse<OrganizationResponse> updateMine(@RequestBody @Valid OrganizationUpdateRequest body) {
        AppUser admin = authContext.requireAdminWithOrganization();
        OrganizationResponse updated = organizationService.updateMine(admin.getUid(), body);
        return ApiResponse.ok("Perfil actualizado.", updated);
    }

    @PostMapping(value = "/me/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Sube/reemplaza el logo de la organización del administrador autenticado")
    public ApiResponse<OrganizationResponse> updateLogo(@RequestPart("logo") MultipartFile logo) {
        AppUser admin = authContext.requireAdminWithOrganization();
        return ApiResponse.ok("Logo actualizado.", organizationService.updateLogo(admin.getUid(), logo));
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }
}
