package com.rescatta.backend.user.service;

import com.rescatta.backend.user.domain.AppUser;

public interface AppUserService {

    /** Devuelve el AppUser del uid dado, creándolo como CIUDADANO si es su primer request. */
    AppUser getOrProvision(String uid, String email, String displayName);

    AppUser findByUid(String uid);
}
