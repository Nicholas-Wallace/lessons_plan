package com.nicholaswallace.lessons_plan.controller;

import java.util.Map;

import com.nicholaswallace.lessons_plan.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ProtectedController {

    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> whoAmI(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> body = Map.of(
            "userId", user.userId(),
            "email", user.email(),
            "role", user.role().name(),
            "plan", user.plan().name()
        );

        return ResponseEntity.ok(body);
    }
}
