package com.rescatta.backend.organization.mapper;

import com.rescatta.backend.organization.domain.Organization;
import com.rescatta.backend.organization.dto.OrganizationResponse;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

    public OrganizationResponse toResponse(Organization org) {
        if (org == null) return null;
        return new OrganizationResponse(
                org.getId(),
                org.getName(),
                org.getType(),
                org.isVerified(),
                org.getDescription(),
                org.getLogoUrl(),
                org.getCoverUrl(),
                org.getAddress(),
                org.getDistrict(),
                org.getLatitude(),
                org.getLongitude(),
                org.getPhone(),
                org.getEmail(),
                org.getWebsite(),
                org.getScheduleText(),
                org.isEmergency24h(),
                org.getCreatedAt()
        );
    }
}
