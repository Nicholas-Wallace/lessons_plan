package com.nicholaswallace.lessons_plan.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.nicholaswallace.lessons_plan.config.AuthProperties;
import com.nicholaswallace.lessons_plan.exception.UnauthorizedException;
import com.nicholaswallace.lessons_plan.model.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class JwtService {

    private final ObjectMapper objectMapper;
    private final AuthProperties authProperties;
    private final Clock clock;
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder decoder = Base64.getUrlDecoder();

    public JwtService(ObjectMapper objectMapper, AuthProperties authProperties, Clock clock) {
        this.objectMapper = objectMapper;
        this.authProperties = authProperties;
        this.clock = clock;
    }

    public GeneratedToken generateToken(AppUser user) {
        Instant issuedAt = Instant.now(clock);
        Instant expiresAt = issuedAt.plus(authProperties.getJwtTtl());

        Map<String, Object> header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
        );

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", String.valueOf(user.getId()));
        payload.put("email", user.getEmail());
        payload.put("role", user.getRole().name());
        payload.put("plan", user.getPlan().name());
        payload.put("iat", issuedAt.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());

        String encodedHeader = encodeJson(header);
        String encodedPayload = encodeJson(payload);
        String content = encodedHeader + "." + encodedPayload;
        String signature = sign(content);

        return new GeneratedToken(content + "." + signature, expiresAt);
    }

    public JwtClaims parse(String token) {
        if (!StringUtils.hasText(token)) {
            throw new UnauthorizedException("INVALID_JWT", "JWT token is required.");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new UnauthorizedException("INVALID_JWT", "JWT token is invalid.");
        }

        String content = parts[0] + "." + parts[1];
        String expectedSignature = sign(content);
        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new UnauthorizedException("INVALID_JWT", "JWT signature is invalid.");
        }

        JsonNode payload = decodePayload(parts[1]);
        Instant expiresAt = Instant.ofEpochSecond(readRequiredLong(payload, "exp"));
        if (!expiresAt.isAfter(Instant.now(clock))) {
            throw new UnauthorizedException("JWT_EXPIRED", "JWT token has expired.");
        }

        return new JwtClaims(
            Long.valueOf(readRequiredText(payload, "sub")),
            readRequiredText(payload, "email"),
            readRequiredText(payload, "role"),
            readRequiredText(payload, "plan"),
            expiresAt
        );
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return encoder.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (JacksonException exception) {
            throw new IllegalStateException("Unable to generate JWT payload.", exception);
        }
    }

    private JsonNode decodePayload(String encodedPayload) {
        try {
            byte[] payloadBytes = decoder.decode(encodedPayload);
            return objectMapper.readTree(payloadBytes);
        } catch (IllegalArgumentException | JacksonException exception) {
            throw new UnauthorizedException("INVALID_JWT", "JWT payload is invalid.");
        }
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(authProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signature = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return encoder.encodeToString(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new IllegalStateException("Unable to sign JWT token.", exception);
        }
    }

    private String readRequiredText(JsonNode payload, String field) {
        String value = payload.path(field).asText(null);
        if (!StringUtils.hasText(value)) {
            throw new UnauthorizedException("INVALID_JWT", "JWT payload is missing required claims.");
        }
        return value;
    }

    private long readRequiredLong(JsonNode payload, String field) {
        JsonNode node = payload.get(field);
        if (node == null || !node.canConvertToLong()) {
            throw new UnauthorizedException("INVALID_JWT", "JWT payload is missing required claims.");
        }
        return node.longValue();
    }

    public record GeneratedToken(String token, Instant expiresAt) {
    }

    public record JwtClaims(Long userId, String email, String role, String plan, Instant expiresAt) {
    }
}
