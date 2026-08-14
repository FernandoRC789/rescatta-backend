package com.rescatta.backend.catalog.repository;

import com.rescatta.backend.catalog.domain.PetPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetPhotoRepository extends JpaRepository<PetPhoto, Long> {
}
