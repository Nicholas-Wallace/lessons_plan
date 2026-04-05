package com.nicholaswallace.lessons_plan.service;

import com.nicholaswallace.lessons_plan.model.AppUser;
import com.nicholaswallace.lessons_plan.repository.AppUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository userRepository;

    public AppUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser createAppUser(AppUser user) {
        boolean AlreadyExists = userRepository.existsByEmail(user.getEmail());
        if(AlreadyExists) {
            throw new RuntimeException("Email already in use");
        }
        return userRepository.save(user);
    }
}
