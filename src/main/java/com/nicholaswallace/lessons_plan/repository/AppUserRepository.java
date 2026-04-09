package com.nicholaswallace.lessons_plan.repository;

import com.nicholaswallace.lessons_plan.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<AppUser> findByEmailIgnoreCase(String email);
}
