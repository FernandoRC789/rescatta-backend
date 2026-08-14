package com.rescatta.backend.breed.dto;

import com.rescatta.backend.shared.domain.enums.Species;

public record BreedResponse(Long id, String name, Species species) {
}
