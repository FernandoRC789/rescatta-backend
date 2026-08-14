package com.rescatta.backend.breed.service;

import com.rescatta.backend.breed.dto.BreedResponse;
import com.rescatta.backend.shared.domain.enums.Species;

import java.util.List;

public interface BreedService {
    List<BreedResponse> listBySpecies(Species species);
}
