package com.nicholaswallace.lessons_plan.repository;

import com.nicholaswallace.lessons_plan.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByEmail(String email);
}
