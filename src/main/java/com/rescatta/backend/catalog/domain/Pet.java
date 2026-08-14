package com.rescatta.backend.catalog.domain;

import com.rescatta.backend.breed.domain.Breed;
import com.rescatta.backend.organization.domain.Organization;
import com.rescatta.backend.shared.domain.enums.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ficha de una mascota ya rescatada, lista (o en proceso) para adopción. Este entregable
 * cubre solo la LECTURA (catálogo + detalle); la publicación/edición por parte del
 * Rescatista/Refugio se agrega en la siguiente entrega junto al resto de endpoints de
 * gestión.
 */
@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Species species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private Breed breed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false, length = 20)
    private AgeGroup ageGroup;

    /** Texto libre para mostrar en la ficha (ej. "2 años", "8 meses"), independiente del filtro por AgeGroup. */
    @Column(name = "age_description", length = 40)
    private String ageDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Size size;

    private Double weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "adoption_status", nullable = false, length = 20)
    @Builder.Default
    private AdoptionStatus adoptionStatus = AdoptionStatus.DISPONIBLE;

    @Column(name = "is_vaccinated", nullable = false)
    @Builder.Default
    private boolean vaccinated = false;

    @Column(name = "is_sterilized", nullable = false)
    @Builder.Default
    private boolean sterilized = false;

    @Column(name = "is_dewormed", nullable = false)
    @Builder.Default
    private boolean dewormed = false;

    @ElementCollection(targetClass = TemperamentTag.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "pet_temperament_tags", joinColumns = @JoinColumn(name = "pet_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag", length = 20)
    @OrderColumn(name = "tag_order") // <--- ¡AÑADE ESTA LÍNEA!
    @Builder.Default
    private List<TemperamentTag> temperamentTags = new ArrayList<>(); // <-- Cambiado Set por List

    /** "Descripción de su nueva salud" — cómo llegó y cómo evolucionó desde el rescate. */
    @Column(name = "health_description", length = 1000)
    private String healthDescription;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(length = 120)
    private String district;

    private Double latitude;

    private Double longitude;

    @Column(name = "adoption_fee_text", length = 30)
    @Builder.Default
    private String adoptionFeeText = "Gratis";

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PetPhoto> photos = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void addPhoto(PetPhoto photo) {
        photos.add(photo);
        photo.setPet(this);
    }
}
