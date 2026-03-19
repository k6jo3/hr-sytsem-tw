package com.company.hrms.notification.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Local Profile 專用配置
 * <p>
 * 提供 WebSocket (SimpMessagingTemplate) 和 JavaMailSender 的備援實作，
 * 避免 local 環境啟動時因缺少基礎設施而失敗。
 * </p>
 * <p>
 * JavaMailSender 預設指向 localhost:1025（MailHog/MailPit），
 * 可在瀏覽器 http://localhost:8025 檢視攔截到的郵件。
 * </p>
 */
@Slf4j
@Configuration
@Profile("local")
public class LocalNotificationConfig {

    @Bean
    @ConditionalOnMissingBean(SimpMessagingTemplate.class)
    public SimpMessagingTemplate simpMessagingTemplate() {
        log.info("[LocalNotificationConfig] 建立 Mock SimpMessagingTemplate (local profile)");
        org.springframework.messaging.support.AbstractSubscribableChannel channel =
                new org.springframework.messaging.support.ExecutorSubscribableChannel();
        return new SimpMessagingTemplate(channel);
    }

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        log.info("[LocalNotificationConfig] 建立 JavaMailSender，指向 localhost:1025 (MailHog/MailPit)");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(1025);
        // MailHog/MailPit 不需要帳號密碼與 TLS
        mailSender.getJavaMailProperties().put("mail.smtp.auth", "false");
        mailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "false");
        return mailSender;
    }
}
