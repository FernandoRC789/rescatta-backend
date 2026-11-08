package com.rescatta.backend.organization.service;

import com.rescatta.backend.common.api.PageResponse;
import com.rescatta.backend.common.exception.BadRequestException;
import com.rescatta.backend.common.exception.ForbiddenException;
import com.rescatta.backend.common.exception.ResourceNotFoundException;
import com.rescatta.backend.common.storage.FileStorageService;
import com.rescatta.backend.common.storage.StorageRollbackCleanup;
import com.rescatta.backend.organization.domain.Organization;
import com.rescatta.backend.organization.dto.OrganizationRegisterRequest;
import com.rescatta.backend.organization.dto.OrganizationResponse;
import com.rescatta.backend.organization.dto.OrganizationUpdateRequest;
import com.rescatta.backend.organization.mapper.OrganizationMapper;
import com.rescatta.backend.organization.repository.OrganizationRepository;
import com.rescatta.backend.shared.domain.enums.UserRole;
import com.rescatta.backend.user.domain.AppUser;
import com.rescatta.backend.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private static final String LOGO_SUBFOLDER = "organizations";

    private final OrganizationRepository organizationRepository;
    private final AppUserRepository appUserRepository;
    private final OrganizationMapper organizationMapper;
    private final FileStorageService fileStorageService;

    @Override
    public OrganizationResponse register(OrganizationRegisterRequest request, String uid, String email, String displayName) {
        AppUser user = appUserRepository.findByUid(uid)
                .orElseGet(() -> AppUser.builder().uid(uid).email(email).displayName(displayName)
                        .role(UserRole.CIUDADANO).build());

        if (user.getOrganization() != null) {
            throw new BadRequestException("Tu cuenta ya está vinculada a una organización.");
        }

        Organization organization = Organization.builder()
                .name(request.name())
                .type(request.type())
                .description(request.description())
                .address(request.address())
                .district(request.district())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .phone(request.phone())
                .email(request.email())
                .website(request.website())
                .scheduleText(request.scheduleText())
                .emergency24h(request.emergency24h())
                .verified(false)
                .build();

        organization = organizationRepository.save(organization);

        user.setRole(UserRole.ADMINISTRADOR);
        user.setOrganization(organization);
        if (email != null) user.setEmail(email);
        if (displayName != null) user.setDisplayName(displayName);
        appUserRepository.save(user);

        return organizationMapper.toResponse(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getById(Long id) {
        return organizationMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrganizationResponse> listVerified(String district, Pageable pageable) {
        Specification<Organization> spec = Specification.<Organization>where((root, query, cb) -> cb.isTrue(root.get("verified")));
        if (district != null && !district.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("district")), district.toLowerCase()));
        }
        Page<Organization> page = organizationRepository.findAll(spec, pageable);
        List<OrganizationResponse> content = page.getContent().stream().map(organizationMapper::toResponse).toList();
        return PageResponse.ofMapped(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrganizationResponse> listPending(Pageable pageable) {
        Page<Organization> page = organizationRepository.findByVerifiedFalse(pageable);
        List<OrganizationResponse> content = page.getContent().stream().map(organizationMapper::toResponse).toList();
        return PageResponse.ofMapped(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getMine(String adminUid) {
        return organizationMapper.toResponse(resolveOrganizationForAdmin(adminUid));
    }

    @Override
    public OrganizationResponse updateMine(String adminUid, OrganizationUpdateRequest request) {
        Organization organization = resolveOrganizationForAdmin(adminUid);
        organization.setName(request.name());
        organization.setType(request.type());
        organization.setDescription(request.description());
        organization.setAddress(request.address());
        organization.setDistrict(request.district());
        organization.setLatitude(request.latitude());
        organization.setLongitude(request.longitude());
        organization.setPhone(request.phone());
        organization.setEmail(request.email());
        organization.setWebsite(request.website());
        organization.setScheduleText(request.scheduleText());
        organization.setEmergency24h(request.emergency24h());
        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Override
    public OrganizationResponse updateLogo(String adminUid, MultipartFile logo) {
        Organization organization = resolveOrganizationForAdmin(adminUid);
        String previousLogoUrl = organization.getLogoUrl();

        String url = fileStorageService.store(logo, LOGO_SUBFOLDER);
        // Si falla el guardado en BD, borramos el logo nuevo.
        StorageRollbackCleanup.deleteOnRollback(fileStorageService, List.of(url));
        // Si el commit es exitoso, borramos el logo anterior para no acumular huérfanos.
        if (previousLogoUrl != null && !previousLogoUrl.equals(url)) {
            StorageRollbackCleanup.deleteAfterCommit(fileStorageService, previousLogoUrl);
        }

        organization.setLogoUrl(url);
        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Override
    public OrganizationResponse verify(Long organizationId, boolean verified) {
        Organization organization = findOrThrow(organizationId);
        organization.setVerified(verified);
        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    // MARK: - Helpers

    private Organization resolveOrganizationForAdmin(String adminUid) {
        AppUser user = appUserRepository.findByUid(adminUid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
        if (user.getOrganization() == null) {
            throw new ForbiddenException("Tu cuenta no está vinculada a ninguna organización.");
        }
        return findOrThrow(user.getOrganization().getId());
    }

    private Organization findOrThrow(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Organización", id));
    }
}
