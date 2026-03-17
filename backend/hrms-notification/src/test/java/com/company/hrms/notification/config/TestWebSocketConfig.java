package com.company.hrms.notification.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * 測試環境 WebSocket 配置
 * <p>
 * 提供 mock 的 SimpMessagingTemplate bean，
 * 替代正式環境的 @EnableWebSocketMessageBroker 配置，
 * 避免測試環境因缺少 WebSocket broker 導致 ApplicationContext 載入失敗。
 * </p>
 */
@Configuration
@Profile("test")
public class TestWebSocketConfig {

    @Bean("brokerMessagingTemplate")
    public SimpMessagingTemplate simpMessagingTemplate() {
        return Mockito.mock(SimpMessagingTemplate.class);
    }
}
