package com.nicholaswallace.lessons_plan.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.nicholaswallace.lessons_plan.config.AuthProperties;
import org.springframework.stereotype.Component;

@Component
public class MagicLinkRedirectBuilder {

    private final AuthProperties authProperties;

    public MagicLinkRedirectBuilder(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public URI buildSuccessRedirect(AuthenticationService.AuthenticatedSession session) {
        String fragment = "token=" + encode(session.token())
            + "&expiresAt=" + encode(session.expiresAt().toString())
            + "&email=" + encode(session.email());

        return URI.create(authProperties.getFrontendSuccessUrl() + "#" + fragment);
    }

    public URI buildErrorRedirect(String errorCode) {
        String fragment = "error=" + encode(errorCode);
        return URI.create(authProperties.getFrontendErrorUrl() + "#" + fragment);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
