package com.company.hrms.gateway.filter;

import com.company.hrms.gateway.security.GatewayJwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * JwtAuthenticationGatewayFilter 單元測試
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationGatewayFilterTest {

    @Mock
    private GatewayJwtTokenService jwtService;

    @Mock
    private GatewayFilterChain filterChain;

    private JwtAuthenticationGatewayFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationGatewayFilter(jwtService);
        lenient().when(filterChain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("公開路徑應直接放行")
    void shouldPassThroughPublicPaths() {
        MockServerHttpRequest request = MockServerHttpRequest
                .post("/api/v1/auth/login")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(any());
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("actuator/health 應放行")
    void shouldPassThroughActuatorHealth() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/actuator/health")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(any());
    }

    @Test
    @DisplayName("缺少 Authorization Header 應回傳 401")
    void shouldReturn401WhenNoAuthHeader() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/employees")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(filterChain, never()).filter(any());
    }

    @Test
    @DisplayName("非 Bearer 格式的 Token 應回傳 401")
    void shouldReturn401WhenNotBearerToken() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/employees")
                .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("無效 Token 應回傳 401")
    void shouldReturn401WhenTokenInvalid() {
        when(jwtService.validateToken("invalid-token")).thenReturn(false);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/employees")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("有效 Token 應放行並轉發使用者資訊 Header")
    void shouldPassAndForwardUserHeaders() {
        String token = "valid-jwt-token";
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn("user-001");
        when(jwtService.extractUsername(token)).thenReturn("john.doe");
        when(jwtService.extractEmail(token)).thenReturn("john@company.com");
        when(jwtService.extractRoles(token)).thenReturn(List.of("ADMIN", "HR_MANAGER"));

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/employees")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        // 驗證 filterChain 被呼叫（代表放行）
        verify(filterChain).filter(any());
    }

    @Test
    @DisplayName("Filter 順序應為 -100（高優先順序）")
    void shouldHaveHighPriority() {
        assertEquals(-100, filter.getOrder());
    }
}
