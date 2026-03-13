package com.company.hrms.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GatewayJwtTokenService 單元測試
 */
class GatewayJwtTokenServiceTest {

    private static final String SECRET = "test-secret-key-for-jwt-token-generation-must-be-at-least-256-bits-long!!";
    private GatewayJwtTokenService jwtService;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new GatewayJwtTokenService(SECRET);
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 建立測試用 Token
     */
    private String createTestToken(String userId, Map<String, Object> claims, long expiryMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId)
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiryMs))
                .signWith(secretKey)
                .compact();
    }

    @Nested
    @DisplayName("validateToken 驗證")
    class ValidateTokenTests {

        @Test
        @DisplayName("有效 Token 應回傳 true")
        void shouldReturnTrueForValidToken() {
            String token = createTestToken("user-001",
                    Map.of("username", "admin", "roles", List.of("ADMIN")),
                    3600000);

            assertTrue(jwtService.validateToken(token));
        }

        @Test
        @DisplayName("過期 Token 應回傳 false")
        void shouldReturnFalseForExpiredToken() {
            String token = createTestToken("user-001",
                    Map.of("username", "admin"),
                    -1000); // 已過期

            assertFalse(jwtService.validateToken(token));
        }

        @Test
        @DisplayName("null Token 應回傳 false")
        void shouldReturnFalseForNullToken() {
            assertFalse(jwtService.validateToken(null));
        }

        @Test
        @DisplayName("空白 Token 應回傳 false")
        void shouldReturnFalseForBlankToken() {
            assertFalse(jwtService.validateToken(""));
            assertFalse(jwtService.validateToken("   "));
        }

        @Test
        @DisplayName("格式錯誤的 Token 應回傳 false")
        void shouldReturnFalseForMalformedToken() {
            assertFalse(jwtService.validateToken("invalid.token.here"));
        }

        @Test
        @DisplayName("使用不同密鑰簽發的 Token 應回傳 false")
        void shouldReturnFalseForTokenWithWrongKey() {
            SecretKey wrongKey = Keys.hmacShaKeyFor(
                    "wrong-secret-key-for-jwt-must-be-at-least-256-bits-long-enough!!!"
                            .getBytes(StandardCharsets.UTF_8));

            String token = Jwts.builder()
                    .subject("user-001")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 3600000))
                    .signWith(wrongKey)
                    .compact();

            assertFalse(jwtService.validateToken(token));
        }
    }

    @Nested
    @DisplayName("Claims 擷取")
    class ExtractClaimsTests {

        private String validToken;

        @BeforeEach
        void setUp() {
            validToken = createTestToken("user-001",
                    Map.of(
                            "username", "john.doe",
                            "email", "john@company.com",
                            "roles", List.of("ADMIN", "HR_MANAGER")
                    ),
                    3600000);
        }

        @Test
        @DisplayName("應正確擷取 userId")
        void shouldExtractUserId() {
            assertEquals("user-001", jwtService.extractUserId(validToken));
        }

        @Test
        @DisplayName("應正確擷取 username")
        void shouldExtractUsername() {
            assertEquals("john.doe", jwtService.extractUsername(validToken));
        }

        @Test
        @DisplayName("應正確擷取 email")
        void shouldExtractEmail() {
            assertEquals("john@company.com", jwtService.extractEmail(validToken));
        }

        @Test
        @DisplayName("應正確擷取 roles")
        void shouldExtractRoles() {
            List<String> roles = jwtService.extractRoles(validToken);
            assertEquals(2, roles.size());
            assertTrue(roles.contains("ADMIN"));
            assertTrue(roles.contains("HR_MANAGER"));
        }

        @Test
        @DisplayName("parseClaims 應回傳完整 Claims")
        void shouldParseAllClaims() {
            Claims claims = jwtService.parseClaims(validToken);
            assertEquals("user-001", claims.getSubject());
            assertEquals("john.doe", claims.get("username", String.class));
            assertNotNull(claims.getIssuedAt());
            assertNotNull(claims.getExpiration());
        }
    }
}
