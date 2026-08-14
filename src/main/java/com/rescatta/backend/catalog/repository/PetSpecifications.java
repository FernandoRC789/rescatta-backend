package com.rescatta.backend.catalog.repository;

import com.rescatta.backend.catalog.domain.Pet;
import com.rescatta.backend.shared.domain.enums.AdoptionStatus;
import com.rescatta.backend.shared.domain.enums.AgeGroup;
import com.rescatta.backend.shared.domain.enums.Size;
import com.rescatta.backend.shared.domain.enums.Species;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filtros del catálogo, componibles con {@code .and(...)}. Corresponden 1:1 a los chips
 * de la pantalla "Catálogo de adopción": Perro / Gato / Cachorro / Cerca de mí, más los
 * filtros adicionales de tamaño y estado de adopción (Sano/En trámite → se resuelve con
 * {@code adoptionStatus}).
 */
public final class PetSpecifications {

    private PetSpecifications() {
    }

    public static Specification<Pet> hasSpecies(Species species) {
        return (root, query, cb) -> species == null ? null : cb.equal(root.get("species"), species);
    }

    public static Specification<Pet> hasAgeGroup(AgeGroup ageGroup) {
        return (root, query, cb) -> ageGroup == null ? null : cb.equal(root.get("ageGroup"), ageGroup);
    }

    public static Specification<Pet> hasSize(Size size) {
        return (root, query, cb) -> size == null ? null : cb.equal(root.get("size"), size);
    }

    public static Specification<Pet> hasAdoptionStatus(AdoptionStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("adoptionStatus"), status);
    }

    /** Por defecto el catálogo no muestra mascotas ya adoptadas, salvo que se pida explícitamente. */
    public static Specification<Pet> excludeAdopted() {
        return (root, query, cb) -> cb.notEqual(root.get("adoptionStatus"), AdoptionStatus.ADOPTADO);
    }
}
