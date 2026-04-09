package com.nicholaswallace.lessons_plan.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;

import com.nicholaswallace.lessons_plan.config.AuthProperties;
import com.nicholaswallace.lessons_plan.exception.BadRequestException;
import com.nicholaswallace.lessons_plan.exception.UnauthorizedException;
import com.nicholaswallace.lessons_plan.model.AppUser;
import com.nicholaswallace.lessons_plan.model.MagicLinkToken;
import com.nicholaswallace.lessons_plan.repository.AppUserRepository;
import com.nicholaswallace.lessons_plan.repository.MagicLinkTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final MagicLinkTokenRepository magicLinkTokenRepository;
    private final SecureTokenService secureTokenService;
    private final MagicLinkEmailSender magicLinkEmailSender;
    private final JwtService jwtService;
    private final AuthProperties authProperties;
    private final Clock clock;

    public AuthenticationService(
        AppUserRepository appUserRepository,
        MagicLinkTokenRepository magicLinkTokenRepository,
        SecureTokenService secureTokenService,
        MagicLinkEmailSender magicLinkEmailSender,
        JwtService jwtService,
        AuthProperties authProperties,
        Clock clock
    ) {
        this.appUserRepository = appUserRepository;
        this.magicLinkTokenRepository = magicLinkTokenRepository;
        this.secureTokenService = secureTokenService;
        this.magicLinkEmailSender = magicLinkEmailSender;
        this.jwtService = jwtService;
        this.authProperties = authProperties;
        this.clock = clock;
    }

    @Transactional
    public SignInLinkDispatch requestSignIn(String email) {
        String normalizedEmail = normalizeEmail(email);
        AppUser user = appUserRepository.findByEmailIgnoreCase(normalizedEmail)
            .orElseThrow(() -> new UnauthorizedException("USER_NOT_FOUND", "User with the provided email was not found."));

        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(authProperties.getMagicLinkTtl());
        String rawToken = secureTokenService.generateToken();

        MagicLinkToken magicLinkToken = new MagicLinkToken();
        magicLinkToken.setUser(user);
        magicLinkToken.setTokenHash(secureTokenService.hash(rawToken));
        magicLinkToken.setCreatedAt(now);
        magicLinkToken.setExpiresAt(expiresAt);
        magicLinkTokenRepository.save(magicLinkToken);

        magicLinkEmailSender.sendSignInLink(user, buildMagicLink(rawToken));
        return new SignInLinkDispatch(expiresAt);
    }

    @Transactional
    public AuthenticatedSession verifyMagicLink(String rawToken) {
        if (!StringUtils.hasText(rawToken)) {
            throw new BadRequestException("MAGIC_LINK_REQUIRED", "Magic link token is required.");
        }

        MagicLinkToken magicLinkToken = magicLinkTokenRepository.findByTokenHash(secureTokenService.hash(rawToken))
            .orElseThrow(() -> new UnauthorizedException("INVALID_MAGIC_LINK", "Authentication link is invalid."));

        Instant now = Instant.now(clock);
        if (magicLinkToken.getUsedAt() != null) {
            throw new UnauthorizedException("MAGIC_LINK_ALREADY_USED", "Authentication link was already used.");
        }
        if (!magicLinkToken.getExpiresAt().isAfter(now)) {
            throw new UnauthorizedException("MAGIC_LINK_EXPIRED", "Authentication link has expired.");
        }

        magicLinkToken.setUsedAt(now);
        JwtService.GeneratedToken generatedToken = jwtService.generateToken(magicLinkToken.getUser());

        AppUser user = magicLinkToken.getUser();
        return new AuthenticatedSession(
            generatedToken.token(),
            generatedToken.expiresAt(),
            user.getId(),
            user.getEmail(),
            user.getRole().name(),
            user.getPlan().name()
        );
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BadRequestException("EMAIL_REQUIRED", "Email is required.");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String buildMagicLink(String rawToken) {
        return UriComponentsBuilder.fromUriString(authProperties.getPublicBaseUrl())
            .path("/auth/magic-link/verify")
            .queryParam("token", rawToken)
            .build()
            .toUriString();
    }

    public record SignInLinkDispatch(Instant expiresAt) {
    }

    public record AuthenticatedSession(
        String token,
        Instant expiresAt,
        Long userId,
        String email,
        String role,
        String plan
    ) {
    }
}
