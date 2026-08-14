package com.rescatta.backend.home.service;

import com.rescatta.backend.home.dto.HomeCitizenSummaryResponse;
import com.rescatta.backend.report.dto.ReportSummaryResponse;
import com.rescatta.backend.report.service.ReportService;
import com.rescatta.backend.shared.domain.enums.ReportCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * El Home no tiene su propia noción de "reportes cercanos": delega por completo en
 * {@link ReportService#listNearby}, para no duplicar la lógica de distancia/orden en dos
 * lugares. Este servicio solo compone esa lista con el conteo de urgencias.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeServiceImpl implements HomeService {

    private static final double DEFAULT_RADIUS_KM = 5.0;
    private static final int DEFAULT_LIMIT = 10;
    private static final Set<ReportCondition> URGENT_CONDITIONS = Set.of(
            ReportCondition.HERIDO, ReportCondition.ATROPELLADO
    );

    private final ReportService reportService;

    @Override
    public HomeCitizenSummaryResponse getCitizenSummary(double latitude, double longitude) {
        List<ReportSummaryResponse> nearby = reportService.listNearby(
                latitude, longitude, DEFAULT_RADIUS_KM, DEFAULT_LIMIT);

        int urgentCount = (int) nearby.stream()
                .filter(report -> URGENT_CONDITIONS.contains(report.condition()))
                .count();

        return new HomeCitizenSummaryResponse(urgentCount, nearby);
    }
}
