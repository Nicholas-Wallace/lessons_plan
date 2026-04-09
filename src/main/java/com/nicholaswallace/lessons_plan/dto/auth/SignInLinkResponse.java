package com.nicholaswallace.lessons_plan.dto.auth;

import java.time.Instant;

public record SignInLinkResponse(String message, Instant expiresAt) {
}
