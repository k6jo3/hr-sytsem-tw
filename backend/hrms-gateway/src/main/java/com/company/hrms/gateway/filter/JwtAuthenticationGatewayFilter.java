package com.company.hrms.gateway.filter;

import com.company.hrms.gateway.security.GatewayJwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Gateway 全域 JWT 認證 Filter
 * 驗證 Token 後將使用者資訊以 Header 轉發給下游微服務
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    private final GatewayJwtTokenService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /** 不需要認證的公開路徑 */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/oauth/**",
            "/api/v1/auth/sso/**",
            "/actuator/health",
            "/actuator/info"
    );

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名單放行
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // 提取 Authorization Header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return unauthorizedResponse(exchange, "缺少認證 Token");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // 驗證 Token
        if (!jwtService.validateToken(token)) {
            return unauthorizedResponse(exchange, "Token 無效或已過期");
        }

        // 提取使用者資訊，轉發至下游服務
        try {
            String userId = jwtService.extractUserId(token);
            String username = jwtService.extractUsername(token);
            String email = jwtService.extractEmail(token);
            List<String> roles = jwtService.extractRoles(token);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Username", username)
                    .header("X-User-Email", email != null ? email : "")
                    .header("X-User-Roles", roles != null ? String.join(",", roles) : "")
                    .build();

            log.debug("JWT 認證通過 - userId: {}, username: {}, path: {}", userId, username, path);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.warn("JWT Claims 解析失敗: {}", e.getMessage());
            return unauthorizedResponse(exchange, "Token 解析失敗");
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    /**
     * 判斷是否為公開路徑
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 回傳 401 錯誤回應
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
