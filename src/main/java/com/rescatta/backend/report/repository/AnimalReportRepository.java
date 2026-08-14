package com.rescatta.backend.report.repository;

import com.rescatta.backend.report.domain.AnimalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnimalReportRepository extends JpaRepository<AnimalReport, Long>,
        JpaSpecificationExecutor<AnimalReport> {

    long countByReporterUid(String reporterUid);
}
