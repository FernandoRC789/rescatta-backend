package com.rescatta.backend.organization.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa al refugio/rescatista que gestiona animales en el catálogo. Deliberadamente
 * NO es un módulo de autenticación (eso lo maneja Firebase Auth desde la app) — esta
 * entidad solo existe para poder mostrar "publicado por Refugio Esperanza ✔" en la ficha
 * de la mascota y en el reporte. La vinculación real usuario-organización (quién puede
 * publicar a nombre de qué refugio) se resolverá cuando se conecte Firebase Auth al
 * backend (siguiente entrega).
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

    @Column(nullable = false)
    private boolean verified;

    private String logoUrl;

    private String district;
}
