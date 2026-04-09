package com.nicholaswallace.lessons_plan.security;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.nicholaswallace.lessons_plan.exception.UnauthorizedException;
import com.nicholaswallace.lessons_plan.model.Plan;
import com.nicholaswallace.lessons_plan.model.Role;
import com.nicholaswallace.lessons_plan.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7).trim();
        try {
            JwtService.JwtClaims claims = jwtService.parse(token);
            AuthenticatedUser principal = new AuthenticatedUser(
                claims.userId(),
                claims.email(),
                Role.valueOf(claims.role()),
                Plan.valueOf(claims.plan())
            );

            UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                    principal,
                    token,
                    List.of(new SimpleGrantedAuthority("ROLE_" + claims.role().toUpperCase(Locale.ROOT)))
                );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UnauthorizedException exception) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
