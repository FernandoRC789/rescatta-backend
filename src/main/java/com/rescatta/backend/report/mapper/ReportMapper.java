package com.rescatta.backend.report.mapper;

import com.rescatta.backend.report.domain.AnimalReport;
import com.rescatta.backend.report.domain.ReportPhoto;
import com.rescatta.backend.report.dto.ReportResponse;
import com.rescatta.backend.report.dto.ReportSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "breedName", expression = "java(report.getBreed() != null ? report.getBreed().getName() : null)")
    @Mapping(target = "photoUrls", expression = "java(toOrderedUrls(report.getPhotos()))")
    ReportResponse toResponse(AnimalReport report);

    default ReportSummaryResponse toSummary(AnimalReport report) {
        return toSummary(report, null);
    }

    default ReportSummaryResponse toSummary(AnimalReport report, Double distanceKm) {
        String cover = report.getPhotos().stream()
                .min(Comparator.comparingInt(ReportPhoto::getPosition))
                .map(ReportPhoto::getUrl)
                .orElse(null);

        return new ReportSummaryResponse(
                report.getId(),
                report.getSpecies(),
                report.getCondition(),
                report.getStatus(),
                report.getAddress(),
                cover,
                distanceKm,
                report.getCreatedAt()
        );
    }

    default List<String> toOrderedUrls(List<ReportPhoto> photos) {
        return photos.stream()
                .sorted(Comparator.comparingInt(ReportPhoto::getPosition))
                .map(ReportPhoto::getUrl)
                .toList();
    }
}
