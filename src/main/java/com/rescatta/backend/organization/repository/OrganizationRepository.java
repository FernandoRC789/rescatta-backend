package com.rescatta.backend.organization.repository;

import com.rescatta.backend.organization.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrganizationRepository extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {
    Page<Organization> findByVerifiedTrue(Pageable pageable);
    Page<Organization> findByVerifiedFalse(Pageable pageable);
}
