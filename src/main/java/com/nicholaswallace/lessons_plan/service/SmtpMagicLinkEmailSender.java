package com.nicholaswallace.lessons_plan.service;

import java.nio.charset.StandardCharsets;

import com.nicholaswallace.lessons_plan.config.AuthProperties;
import com.nicholaswallace.lessons_plan.model.AppUser;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConditionalOnBean(JavaMailSender.class)
public class SmtpMagicLinkEmailSender implements MagicLinkEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpMagicLinkEmailSender.class);

    private final JavaMailSender javaMailSender;
    private final AuthProperties authProperties;
    private final MailProperties mailProperties;

    public SmtpMagicLinkEmailSender(JavaMailSender javaMailSender, AuthProperties authProperties, MailProperties mailProperties) {
        this.javaMailSender = javaMailSender;
        this.authProperties = authProperties;
        this.mailProperties = mailProperties;
    }

    @Override
    public void sendSignInLink(AppUser user, String magicLink) {
        if (!StringUtils.hasText(mailProperties.getHost())) {
            LOGGER.info("Magic link for {}: {}", user.getEmail(), magicLink);
            return;
        }

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
            helper.setFrom(authProperties.getMailFrom());
            helper.setTo(user.getEmail());
            helper.setSubject("Seu link de acesso");
            helper.setText(buildMessage(user.getEmail(), magicLink), true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new IllegalStateException("Unable to send the sign-in email.", exception);
        }
    }

    private String buildMessage(String email, String magicLink) {
        return """
            <html>
              <body style="font-family: Arial, sans-serif; color: #1f2937;">
                <h2>Entrar no Lessons Plan</h2>
                <p>Recebemos uma tentativa de login para o email %s.</p>
                <p>O link abaixo expira em breve e pode ser usado apenas uma vez.</p>
                <p>
                  <a href="%s"
                     style="display:inline-block;padding:12px 24px;background:#0f766e;color:#ffffff;text-decoration:none;border-radius:8px;">
                    Entrar agora
                  </a>
                </p>
                <p>Se o botao nao funcionar, copie e cole este link no navegador:</p>
                <p>%s</p>
              </body>
            </html>
            """.formatted(email, magicLink, magicLink);
    }
}
