package com.nicholaswallace.lessons_plan.controller;

import com.nicholaswallace.lessons_plan.model.AppUser;
import com.nicholaswallace.lessons_plan.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class AppUserController {

    private final AppUserService userService;

    public AppUserController(AppUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<AppUser> create(@RequestBody AppUser user){
        AppUser created = userService.createAppUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
