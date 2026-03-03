package com.company.hrms.iam.infrastructure.config;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;

/**
 * Local profile 用的 No-Op 郵件發送器
 * 不實際發送郵件，僅記錄日誌
 */
@Configuration
@Profile("local")
public class NoOpMailConfig {

    private static final Logger log = LoggerFactory.getLogger(NoOpMailConfig.class);

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSender() {
            @Override
            public MimeMessage createMimeMessage() {
                throw new UnsupportedOperationException("Local profile 不支援 MimeMessage");
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
                throw new UnsupportedOperationException("Local profile 不支援 MimeMessage");
            }

            @Override
            public void send(MimeMessage... mimeMessages) throws MailException {
                log.info("[LOCAL] 模擬發送 MimeMessage（共 {} 封）", mimeMessages.length);
            }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
                log.info("[LOCAL] 模擬發送 MimeMessagePreparator（共 {} 封）", mimeMessagePreparators.length);
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) throws MailException {
                for (SimpleMailMessage msg : simpleMessages) {
                    log.info("[LOCAL] 模擬發送郵件: to={}, subject={}",
                            msg.getTo(), msg.getSubject());
                }
            }

            @Override
            public void send(SimpleMailMessage simpleMessage) throws MailException {
                log.info("[LOCAL] 模擬發送郵件: to={}, subject={}",
                        simpleMessage.getTo(), simpleMessage.getSubject());
            }
        };
    }
}
