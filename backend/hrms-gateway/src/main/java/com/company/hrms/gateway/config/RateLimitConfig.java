package com.company.hrms.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 限流 Key 解析器配置
 *
 * <p>提供兩種 KeyResolver：</p>
 * <ul>
 *   <li>{@code userKeyResolver}（預設）— 已認證用戶以 userId 為 key，未認證則降級為 IP</li>
 *   <li>{@code ipKeyResolver} — 純以客戶端 IP 為 key，用於登入等公開端點的暴力破解防護</li>
 * </ul>
 */
@Slf4j
@Configuration
public class RateLimitConfig {

    /**
     * 預設限流 Key 解析器：優先使用 userId，降級為 IP
     * 適用於一般 API 端點
     */
    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null && !userId.isBlank()) {
                return Mono.just("user:" + userId);
            }
            return Mono.just("ip:" + resolveClientIp(exchange));
        };
    }

    /**
     * IP 限流 Key 解析器：純以客戶端 IP 為 key
     * 適用於登入、註冊等公開端點，防止暴力破解
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just("ip:" + resolveClientIp(exchange));
    }

    /**
     * 解析客戶端真實 IP（支援反向代理 X-Forwarded-For）
     */
    private String resolveClientIp(org.springframework.web.server.ServerWebExchange exchange) {
        // 優先從 X-Forwarded-For 取得（反向代理場景）
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // X-Forwarded-For 可能包含多個 IP，取第一個（最原始的客戶端 IP）
            String clientIp = xForwardedFor.split(",")[0].trim();
            if (!clientIp.isEmpty()) {
                return clientIp;
            }
        }

        // X-Real-IP（Nginx 常用）
        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }

        // 降級為直接連線 IP
        var remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress != null) {
            return Objects.requireNonNull(remoteAddress.getAddress()).getHostAddress();
        }
        return "unknown";
    }
}
