package com.rescatta.backend.organization.controller;

import com.rescatta.backend.common.api.ApiResponse;
import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.common.security.AuthContext;
import com.rescatta.backend.organization.dto.OrganizationResponse;
import com.rescatta.backend.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Validación de nuevas veterinarias/refugios por parte del staff de Rescatta
 * (rol SUPERADMIN). Una organización recién registrada no aparece en el directorio
 * público ni puede operar reportes hasta que sea verificada aquí.
 */
@RestController
@RequestMapping("/api/v1/superadmin/organizations")
@RequiredArgsConstructor
@Tag(name = "Rescatta Admin", description = "Verificación de organizaciones — solo staff de Rescatta")
public class SuperAdminOrganizationController {

    private final OrganizationService organizationService;
    private final AuthContext authContext;

    @GetMapping("/pending")
    @Operation(summary = "Lista organizaciones pendientes de verificación")
    public ApiResponse<PageResponse<OrganizationResponse>> pending(Pageable pageable) {
        authContext.requireSuperAdmin();
        return ApiResponse.ok(organizationService.listPending(pageable));
    }

    @PatchMapping("/{id}/verify")
    @Operation(summary = "Aprueba (o revoca) la verificación de una organización")
    public ApiResponse<OrganizationResponse> verify(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean verified) {
        authContext.requireSuperAdmin();
        String message = verified ? "Organización verificada." : "Verificación revocada.";
        return ApiResponse.ok(message, organizationService.verify(id, verified));
    }
}
