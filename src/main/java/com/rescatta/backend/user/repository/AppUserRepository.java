package com.rescatta.backend.user.repository;

import com.rescatta.backend.user.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUid(String uid);
    boolean existsByUid(String uid);
}
