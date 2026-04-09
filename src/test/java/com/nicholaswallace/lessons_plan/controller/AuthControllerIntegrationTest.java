package com.nicholaswallace.lessons_plan.controller;

import java.net.URI;
import java.time.Instant;

import com.nicholaswallace.lessons_plan.model.AppUser;
import com.nicholaswallace.lessons_plan.model.MagicLinkToken;
import com.nicholaswallace.lessons_plan.model.Plan;
import com.nicholaswallace.lessons_plan.model.Role;
import com.nicholaswallace.lessons_plan.repository.AppUserRepository;
import com.nicholaswallace.lessons_plan.repository.MagicLinkTokenRepository;
import com.nicholaswallace.lessons_plan.service.JwtService;
import com.nicholaswallace.lessons_plan.service.MagicLinkEmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private MagicLinkTokenRepository magicLinkTokenRepository;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private MagicLinkEmailSender magicLinkEmailSender;

    @BeforeEach
    void setUp() {
        magicLinkTokenRepository.deleteAll();
        appUserRepository.deleteAll();
        doNothing().when(magicLinkEmailSender).sendSignInLink(any(AppUser.class), any(String.class));
    }

    @Test
    void shouldSendMagicLinkWhenUserExists() throws Exception {
        createUser("teacher@example.com");

        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"teacher@example.com"}
                    """))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.message").value("Authentication link sent."))
            .andExpect(jsonPath("$.expiresAt").exists());

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
        verify(magicLinkEmailSender).sendSignInLink(userCaptor.capture(), linkCaptor.capture());

        assertThat(userCaptor.getValue().getEmail()).isEqualTo("teacher@example.com");
        assertThat(linkCaptor.getValue()).startsWith("http://localhost:8080/auth/magic-link/verify?token=");
        assertThat(magicLinkTokenRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldReturnUnauthorizedWhenEmailDoesNotExist() throws Exception {
        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"missing@example.com"}
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));

        verify(magicLinkEmailSender, never()).sendSignInLink(any(AppUser.class), any(String.class));
    }

    @Test
    void shouldVerifyMagicLinkAndRedirectWithJwtInFragment() throws Exception {
        createUser("teacher@example.com");

        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"teacher@example.com"}
                    """))
            .andExpect(status().isAccepted());

        String magicLink = captureMagicLink();
        String token = extractToken(magicLink);

        String redirectLocation = mockMvc.perform(get("/auth/magic-link/verify").param("token", token))
            .andExpect(status().isFound())
            .andExpect(header().string(HttpHeaders.LOCATION, org.hamcrest.Matchers.startsWith("http://localhost:3000/auth/callback#token=")))
            .andReturn()
            .getResponse()
            .getHeader(HttpHeaders.LOCATION);

        assertThat(redirectLocation).isNotBlank();
        assertThat(findStoredMagicLink().getUsedAt()).isNotNull();

        String jwt = extractFragmentValue(redirectLocation, "token");
        JwtService.JwtClaims claims = jwtService.parse(jwt);
        assertThat(claims.email()).isEqualTo("teacher@example.com");
    }

    @Test
    void shouldRedirectToErrorPageWhenMagicLinkExpires() throws Exception {
        createUser("teacher@example.com");

        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"teacher@example.com"}
                    """))
            .andExpect(status().isAccepted());

        MagicLinkToken storedToken = findStoredMagicLink();
        storedToken.setExpiresAt(Instant.now().minusSeconds(60));
        magicLinkTokenRepository.save(storedToken);

        String magicLink = captureMagicLink();
        String token = extractToken(magicLink);

        mockMvc.perform(get("/auth/magic-link/verify").param("token", token))
            .andExpect(status().isFound())
            .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost:3000/auth/error#error=MAGIC_LINK_EXPIRED"));
    }

    private AppUser createUser(String email) {
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPassword("secret");
        user.setRole(Role.User);
        user.setPlan(Plan.Basic);
        return appUserRepository.save(user);
    }

    private MagicLinkToken findStoredMagicLink() {
        return magicLinkTokenRepository.findAll().stream()
            .findFirst()
            .orElseThrow();
    }

    private String captureMagicLink() {
        ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
        verify(magicLinkEmailSender).sendSignInLink(any(AppUser.class), linkCaptor.capture());
        return linkCaptor.getValue();
    }

    private String extractToken(String magicLink) {
        String query = URI.create(magicLink).getQuery();
        return query.substring("token=".length());
    }

    private String extractFragmentValue(String redirectLocation, String key) {
        String fragment = URI.create(redirectLocation).getFragment();
        String prefix = key + "=";
        for (String entry : fragment.split("&")) {
            if (entry.startsWith(prefix)) {
                return java.net.URLDecoder.decode(entry.substring(prefix.length()), java.nio.charset.StandardCharsets.UTF_8);
            }
        }
        throw new IllegalStateException("Missing fragment key: " + key);
    }
}
