package com.rescatta.backend.organization.service;

import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.organization.dto.OrganizationRegisterRequest;
import com.rescatta.backend.organization.dto.OrganizationResponse;
import com.rescatta.backend.organization.dto.OrganizationUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface OrganizationService {

    OrganizationResponse register(OrganizationRegisterRequest request, String uid, String email, String displayName);

    OrganizationResponse getById(Long id);

    PageResponse<OrganizationResponse> listVerified(String district, Pageable pageable);

    PageResponse<OrganizationResponse> listPending(Pageable pageable);

    OrganizationResponse getMine(String adminUid);

    OrganizationResponse updateMine(String adminUid, OrganizationUpdateRequest request);

    OrganizationResponse updateLogo(String adminUid, MultipartFile logo);

    OrganizationResponse verify(Long organizationId, boolean verified);
}
