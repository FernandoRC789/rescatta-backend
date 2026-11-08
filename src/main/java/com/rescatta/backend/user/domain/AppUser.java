package com.rescatta.backend.user.domain;

import com.rescatta.backend.organization.domain.Organization;
import com.rescatta.backend.shared.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Identidad interna de cada usuario autenticado con Firebase (tanto ciudadanos de la
 * app iOS como administradores del panel web). Se crea automáticamente en el primer
 * request autenticado ({@code AppUserService#getOrProvision}) con rol CIUDADANO por
 * defecto; el rol ADMINISTRADOR se asigna al registrar una organización
 * ({@code OrganizationService#register}).
 */
@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** UID de Firebase Authentication — mismo valor que usa la app iOS y el panel web. */
    @Column(nullable = false, unique = true, length = 128)
    private String uid;

    @Column(length = 180)
    private String email;

    @Column(name = "display_name", length = 150)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.CIUDADANO;

    /** Solo aplica cuando role = ADMINISTRADOR: a qué veterinaria/refugio pertenece. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(length = 30)
    private String phone;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public boolean isAdmin() {
        return role == UserRole.ADMINISTRADOR || role == UserRole.SUPERADMIN;
    }
}
