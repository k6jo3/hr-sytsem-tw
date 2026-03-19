package com.company.hrms.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 安全性 HTTP 標頭 Filter
 *
 * <p>為所有回應注入常見的安全性標頭，防禦 XSS、MIME 嗅探、Clickjacking 等攻擊。</p>
 *
 * <p>注入的標頭：</p>
 * <ul>
 *   <li>{@code X-Content-Type-Options: nosniff} — 防止瀏覽器 MIME 嗅探</li>
 *   <li>{@code X-Frame-Options: DENY} — 防止 Clickjacking（iframe 嵌入）</li>
 *   <li>{@code X-XSS-Protection: 1; mode=block} — 啟用瀏覽器 XSS 防護</li>
 *   <li>{@code Referrer-Policy: strict-origin-when-cross-origin} — 控制 Referrer 資訊洩漏</li>
 *   <li>{@code Permissions-Policy: camera=(), microphone=(), geolocation=()} — 限制瀏覽器功能存取</li>
 *   <li>{@code Cache-Control: no-store} — 防止敏感 API 回應被快取（僅限 API 路徑）</li>
 * </ul>
 */
@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();

            // 防止 MIME 嗅探
            headers.addIfAbsent("X-Content-Type-Options", "nosniff");

            // 防止 Clickjacking
            headers.addIfAbsent("X-Frame-Options", "DENY");

            // XSS 防護（瀏覽器內建）
            headers.addIfAbsent("X-XSS-Protection", "1; mode=block");

            // Referrer 資訊控制
            headers.addIfAbsent("Referrer-Policy", "strict-origin-when-cross-origin");

            // 限制瀏覽器功能存取權限
            headers.addIfAbsent("Permissions-Policy", "camera=(), microphone=(), geolocation=()");

            // API 路徑禁止快取（防止敏感資料被瀏覽器或中介代理快取）
            String path = exchange.getRequest().getURI().getPath();
            if (path.startsWith("/api/")) {
                headers.addIfAbsent("Cache-Control", "no-store");
                headers.addIfAbsent("Pragma", "no-cache");
            }
        }));
    }

    @Override
    public int getOrder() {
        // 在其他 Filter 之後執行（較低優先順序），確保安全標頭最終被加入
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
