package com.rescatta.backend.report.dto;

import com.rescatta.backend.shared.domain.enums.AgeGroup;
import com.rescatta.backend.shared.domain.enums.ReportCondition;
import com.rescatta.backend.shared.domain.enums.ReportStatus;
import com.rescatta.backend.shared.domain.enums.Species;

import java.time.Instant;
import java.util.List;

public record ReportResponse(
        Long id,
        Species species,
        AgeGroup ageGroup,
        String breedName,
        ReportCondition condition,
        String description,
        Double latitude,
        Double longitude,
        String address,
        ReportStatus status,
        boolean reporterCanStay,
        List<String> photoUrls,
        Instant createdAt
) {
}
