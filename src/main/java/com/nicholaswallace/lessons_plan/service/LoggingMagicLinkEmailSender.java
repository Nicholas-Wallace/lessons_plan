package com.nicholaswallace.lessons_plan.service;

import com.nicholaswallace.lessons_plan.model.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(JavaMailSender.class)
public class LoggingMagicLinkEmailSender implements MagicLinkEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingMagicLinkEmailSender.class);

    @Override
    public void sendSignInLink(AppUser user, String magicLink) {
        LOGGER.info("Magic link for {}: {}", user.getEmail(), magicLink);
    }
}
