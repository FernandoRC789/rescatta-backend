package com.rescatta.backend.home.service;

import com.rescatta.backend.home.dto.HomeCitizenSummaryResponse;

public interface HomeService {
    HomeCitizenSummaryResponse getCitizenSummary(double latitude, double longitude);
}
