package com.nicholaswallace.lessons_plan.repository;

import java.util.Optional;

import com.nicholaswallace.lessons_plan.model.MagicLinkToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagicLinkTokenRepository extends JpaRepository<MagicLinkToken, Long> {
    Optional<MagicLinkToken> findByTokenHash(String tokenHash);
}
