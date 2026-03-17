package com.company.hrms.notification.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket STOMP 配置
 * <p>
 * 啟用 WebSocket Message Broker，提供 SimpMessagingTemplate bean，
 * 供 InAppChannelSender 透過 STOMP 發送即時站內通知。
 * </p>
 */
@Configuration
@EnableWebSocketMessageBroker
@Profile("!test")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 啟用簡易記憶體 broker，處理 /topic 和 /queue 前綴的目的地
        config.enableSimpleBroker("/topic", "/queue");
        // 應用程式目的地前綴（客戶端發送訊息時使用）
        config.setApplicationDestinationPrefixes("/app");
        // 使用者目的地前綴（點對點通知使用）
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 註冊 STOMP 端點，前端透過此端點建立 WebSocket 連線
        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
