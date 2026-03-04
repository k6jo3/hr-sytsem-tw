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
 * 提供 WebSocket (SimpMessagingTemplate) 和 JavaMailSender 的 Mock 實作，
 * 避免 local 環境啟動時因缺少基礎設施而失敗。
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
        log.info("[LocalNotificationConfig] 建立 Mock JavaMailSender (local profile)");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(25);
        return mailSender;
    }
}
