package com.nicholaswallace.lessons_plan.controller;

import java.net.URI;

import com.nicholaswallace.lessons_plan.dto.auth.SignInLinkResponse;
import com.nicholaswallace.lessons_plan.dto.auth.SignInRequest;
import com.nicholaswallace.lessons_plan.exception.ApiException;
import com.nicholaswallace.lessons_plan.service.AuthenticationService;
import com.nicholaswallace.lessons_plan.service.MagicLinkRedirectBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final MagicLinkRedirectBuilder magicLinkRedirectBuilder;

    public AuthController(AuthenticationService authenticationService, MagicLinkRedirectBuilder magicLinkRedirectBuilder) {
        this.authenticationService = authenticationService;
        this.magicLinkRedirectBuilder = magicLinkRedirectBuilder;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInLinkResponse> signIn(@RequestBody SignInRequest request) {
        AuthenticationService.SignInLinkDispatch dispatch = authenticationService.requestSignIn(request.email());
        SignInLinkResponse response = new SignInLinkResponse("Authentication link sent.", dispatch.expiresAt());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/magic-link/verify")
    public ResponseEntity<Void> verifyMagicLink(@RequestParam String token) {
        try {
            AuthenticationService.AuthenticatedSession session = authenticationService.verifyMagicLink(token);
            URI redirectUri = magicLinkRedirectBuilder.buildSuccessRedirect(session);
            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
        } catch (ApiException exception) {
            URI redirectUri = magicLinkRedirectBuilder.buildErrorRedirect(exception.getCode());
            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
        }
    }
}
