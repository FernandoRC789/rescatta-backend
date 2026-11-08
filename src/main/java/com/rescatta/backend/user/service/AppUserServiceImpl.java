package com.rescatta.backend.user.service;

import com.rescatta.backend.common.exception.ResourceNotFoundException;
import com.rescatta.backend.shared.domain.enums.UserRole;
import com.rescatta.backend.user.domain.AppUser;
import com.rescatta.backend.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;

    @Override
    public AppUser getOrProvision(String uid, String email, String displayName) {
        return appUserRepository.findByUid(uid)
                .map(existing -> refreshContactInfo(existing, email, displayName))
                .orElseGet(() -> appUserRepository.save(
                        AppUser.builder()
                                .uid(uid)
                                .email(email)
                                .displayName(displayName)
                                .role(UserRole.CIUDADANO)
                                .build()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public AppUser findByUid(String uid) {
        return appUserRepository.findByUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

    /** Mantiene email/nombre sincronizados con lo último que reportó Firebase, sin pisar el rol. */
    private AppUser refreshContactInfo(AppUser user, String email, String displayName) {
        boolean changed = false;
        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
            changed = true;
        }
        if (displayName != null && !displayName.equals(user.getDisplayName())) {
            user.setDisplayName(displayName);
            changed = true;
        }
        return changed ? appUserRepository.save(user) : user;
    }
}
