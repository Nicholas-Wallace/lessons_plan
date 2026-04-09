package com.nicholaswallace.lessons_plan.config;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

    private String publicBaseUrl = "http://localhost:8080";
    private String frontendSuccessUrl = "http://localhost:3000/auth/callback";
    private String frontendErrorUrl = "http://localhost:3000/auth/error";
    private String mailFrom = "no-reply@lessonsplan.local";
    private Duration magicLinkTtl = Duration.ofMinutes(15);
    private Duration jwtTtl = Duration.ofHours(8);
    private String jwtSecret = "change-me-change-me-change-me-change-me";
}
