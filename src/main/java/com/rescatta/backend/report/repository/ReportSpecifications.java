package com.rescatta.backend.report.repository;

import com.rescatta.backend.report.domain.AnimalReport;
import com.rescatta.backend.shared.domain.enums.ReportStatus;
import com.rescatta.backend.shared.domain.enums.Species;
import org.springframework.data.jpa.domain.Specification;

/**
 * Cada filtro es una {@link Specification} independiente y componible con
 * {@code .and(...)}. Esto evita el clásico método de repositorio con 6 parámetros
 * opcionales y un enjambre de {@code if (x != null)} — cada filtro se agrega solo si
 * el valor llegó, y el resto del código no necesita saberlo.
 */
public final class ReportSpecifications {

    private ReportSpecifications() {
    }

    public static Specification<AnimalReport> hasSpecies(Species species) {
        return (root, query, cb) -> species == null ? null : cb.equal(root.get("species"), species);
    }

    public static Specification<AnimalReport> hasStatus(ReportStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<AnimalReport> reportedBy(String reporterUid) {
        return (root, query, cb) -> reporterUid == null ? null : cb.equal(root.get("reporterUid"), reporterUid);
    }
}
