package com.nicholaswallace.lessons_plan.service;

import com.nicholaswallace.lessons_plan.exception.BadRequestException;
import com.nicholaswallace.lessons_plan.model.AppUser;
import com.nicholaswallace.lessons_plan.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Service
public class AppUserService {

    private final AppUserRepository userRepository;

    public AppUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser createAppUser(AppUser user) {
        String normalizedEmail = normalizeEmail(user.getEmail());
        boolean alreadyExists = userRepository.existsByEmailIgnoreCase(normalizedEmail);
        if (alreadyExists) {
            throw new BadRequestException("EMAIL_ALREADY_IN_USE", "Email already in use.");
        }

        user.setEmail(normalizedEmail);
        return userRepository.save(user);
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BadRequestException("EMAIL_REQUIRED", "Email is required.");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
