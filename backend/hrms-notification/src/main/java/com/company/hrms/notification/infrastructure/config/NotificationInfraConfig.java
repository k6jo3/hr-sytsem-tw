package com.company.hrms.notification.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 通知服務基礎設施配置
 *
 * @author Claude
 * @since 2026-01-29
 */
@Configuration
public class NotificationInfraConfig {

    /**
     * RestTemplate Bean
     * <p>
     * 用於發送 HTTP 請求至外部服務（LINE Notify、Teams Webhook 等）
     * </p>
     *
     * @return RestTemplate 實例
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
