package com.rescatta.backend.report.domain;

import com.rescatta.backend.breed.domain.Breed;
import com.rescatta.backend.shared.domain.enums.AgeGroup;
import com.rescatta.backend.shared.domain.enums.ReportCondition;
import com.rescatta.backend.shared.domain.enums.ReportStatus;
import com.rescatta.backend.shared.domain.enums.Species;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Reporte de un animal visto en la calle — el corazón del flujo "Reportar animal" de
 * la app. Se crea desde el rol Ciudadano y luego es atendido por un Rescatista/Refugio
 * (esa transición de estado se agregará en la siguiente entrega, junto al módulo de
 * gestión de reportes del rescatista).
 */
@Entity
@Table(name = "animal_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Species species;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", length = 20)
    private AgeGroup ageGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private Breed breed;

    @Enumerated(EnumType.STRING)
    @Column(name = "animal_condition", nullable = false, length = 20)
    private ReportCondition condition;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDIENTE;

    /** UID de Firebase del ciudadano que reportó — ver {@code CurrentUserProvider}. */
    @Column(name = "reporter_uid", nullable = false, length = 128)
    private String reporterUid;

    @Column(name = "reporter_can_stay", nullable = false)
    @Builder.Default
    private boolean reporterCanStay = false;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReportPhoto> photos = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Mantiene sincronizada la relación bidireccional al agregar fotos. */
    public void addPhoto(ReportPhoto photo) {
        photos.add(photo);
        photo.setReport(this);
    }
}
