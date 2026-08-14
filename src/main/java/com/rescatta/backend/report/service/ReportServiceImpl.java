package com.rescatta.backend.report.service;

import com.rescatta.backend.breed.domain.Breed;
import com.rescatta.backend.breed.repository.BreedRepository;
import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.common.exception.BadRequestException;
import com.rescatta.backend.common.exception.ResourceNotFoundException;
import com.rescatta.backend.common.storage.FileStorageService;
import com.rescatta.backend.common.util.GeoUtils;
import com.rescatta.backend.report.domain.AnimalReport;
import com.rescatta.backend.report.domain.ReportPhoto;
import com.rescatta.backend.report.dto.CreateReportRequest;
import com.rescatta.backend.report.dto.ReportResponse;
import com.rescatta.backend.report.dto.ReportSummaryResponse;
import com.rescatta.backend.report.mapper.ReportMapper;
import com.rescatta.backend.report.repository.AnimalReportRepository;
import com.rescatta.backend.report.repository.ReportSpecifications;
import com.rescatta.backend.shared.domain.enums.ReportStatus;
import com.rescatta.backend.shared.domain.enums.Species;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private static final String PHOTO_SUBFOLDER = "reports";

    private final AnimalReportRepository reportRepository;
    private final BreedRepository breedRepository;
    private final FileStorageService fileStorageService;
    private final ReportMapper reportMapper;
    private final ReportProperties reportProperties;

    @Override
    public ReportResponse createReport(CreateReportRequest request, List<MultipartFile> photos, String reporterUid) {
        validatePhotos(photos);

        Breed breed = resolveBreed(request.breedId());

        AnimalReport report = AnimalReport.builder()
                .species(request.species())
                .ageGroup(request.ageGroup())
                .breed(breed)
                .condition(request.condition())
                .description(request.description())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .address(request.address())
                .reporterCanStay(request.reporterCanStay())
                .reporterUid(reporterUid)
                .status(ReportStatus.PENDIENTE)
                .build();

        List<String> photoUrls = fileStorageService.storeAll(photos, PHOTO_SUBFOLDER);
        for (int i = 0; i < photoUrls.size(); i++) {
            report.addPhoto(ReportPhoto.builder().url(photoUrls.get(i)).position(i).build());
        }

        AnimalReport saved = reportRepository.save(report);
        return reportMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse getById(Long id) {
        return reportMapper.toResponse(findReportOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportSummaryResponse> list(Species species, ReportStatus status, Pageable pageable) {
        Specification<AnimalReport> spec = Specification
                .where(ReportSpecifications.hasSpecies(species))
                .and(ReportSpecifications.hasStatus(status));

        Page<AnimalReport> page = reportRepository.findAll(spec, pageable);
        List<ReportSummaryResponse> content = page.getContent().stream().map(reportMapper::toSummary).toList();
        return PageResponse.ofMapped(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportSummaryResponse> listNearby(double latitude, double longitude, double radiusKm, int limit) {
        // Solo reportes activos (no rescatados aún) son relevantes para "cerca de mí".
        Specification<AnimalReport> spec = (root, query, cb) -> cb.notEqual(root.get("status"), ReportStatus.RESCATADO);

        return reportRepository.findAll(spec).stream()
                .map(report -> new ReportDistance(report,
                        GeoUtils.distanceKm(latitude, longitude, report.getLatitude(), report.getLongitude())))
                .filter(pair -> pair.distanceKm() <= radiusKm)
                .sorted(Comparator.comparingDouble(ReportDistance::distanceKm))
                .limit(limit)
                .map(pair -> reportMapper.toSummary(pair.report(), roundTo1Decimal(pair.distanceKm())))
                .toList();
    }

    /** Par auxiliar reporte+distancia, solo usado para ordenar por cercanía en {@link #listNearby}. */
    private record ReportDistance(AnimalReport report, double distanceKm) {
    }

    @Override
    @Transactional(readOnly = true)
    public long countByReporter(String reporterUid) {
        return reportRepository.countByReporterUid(reporterUid);
    }

    // MARK: - Helpers

    private AnimalReport findReportOrThrow(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Reporte", id));
    }

    private Breed resolveBreed(Long breedId) {
        if (breedId == null) {
            return null;
        }
        return breedRepository.findById(breedId)
                .orElseThrow(() -> ResourceNotFoundException.of("Raza", breedId));
    }

    private void validatePhotos(List<MultipartFile> photos) {
        if (photos == null || photos.isEmpty()) {
            throw new BadRequestException("Agrega al menos una foto del animal.");
        }
        if (photos.size() > reportProperties.maxPhotos()) {
            throw new BadRequestException("Puedes subir un máximo de " + reportProperties.maxPhotos() + " fotos.");
        }
    }

    private double roundTo1Decimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
