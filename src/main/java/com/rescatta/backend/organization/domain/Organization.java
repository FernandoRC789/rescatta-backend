package com.rescatta.backend.organization.domain;

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
 * Veterinaria o refugio que gestiona reportes y publica animales en el catálogo.
 * Se crea al registrarse desde el panel web ({@code OrganizationService#register}),
 * queda {@code verified = false} hasta que un SUPERADMIN de Rescatta la valida, y desde
 * ese momento su equipo (usuarios con role ADMINISTRADOR vinculados a esta organización)
 * puede operar el panel: revisar reportes, actualizar su estado y chatear con los
 * ciudadanos que reportaron.
 */
@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    /** VETERINARIA o REFUGIO — informativo, no cambia el comportamiento de la API. */
    @Column(length = 30)
    private String type;

    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false;

    @Column(length = 1000)
    private String description;

    private String logoUrl;

    private String coverUrl;

    @Column(length = 255)
    private String address;

    private String district;

    private Double latitude;

    private Double longitude;

    @Column(length = 30)
    private String phone;

    @Column(length = 180)
    private String email;

    private String website;

    /** Texto libre, ej. "Lun-Vie 9:00-18:00, Sáb 9:00-13:00". */
    @Column(name = "schedule_text", length = 255)
    private String scheduleText;

    /** Capacidad de atención de emergencias 24h (mostrado con una insignia en la app). */
    @Column(name = "emergency_24h", nullable = false)
    @Builder.Default
    private boolean emergency24h = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
