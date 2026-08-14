package com.rescatta.backend.breed.repository;

import com.rescatta.backend.breed.domain.Breed;
import com.rescatta.backend.shared.domain.enums.Species;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BreedRepository extends JpaRepository<Breed, Long> {
    List<Breed> findBySpeciesOrderByNameAsc(Species species);
}
