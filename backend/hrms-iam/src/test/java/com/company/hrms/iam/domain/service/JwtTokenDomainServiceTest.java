package com.company.hrms.iam.domain.service;

import com.company.hrms.iam.domain.model.aggregate.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtTokenDomainService 單元測試
 * 測試 JWT Token 產生與驗證邏輯
 */
@DisplayName("JwtTokenDomainService 測試")
class JwtTokenDomainServiceTest {

    private JwtTokenDomainService jwtService;

    // 測試用密鑰 (至少 256 bits / 32 bytes for HS256)
    private static final String TEST_SECRET = "test-secret-key-for-jwt-token-generation-must-be-long-enough-256-bits";
    private static final long ACCESS_TOKEN_EXPIRY = 3600000L; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRY = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        jwtService = new JwtTokenDomainService(TEST_SECRET, ACCESS_TOKEN_EXPIRY, REFRESH_TOKEN_EXPIRY);
    }

    @Nested
    @DisplayName("Access Token 產生")
    class GenerateAccessTokenTests {

        @Test
        @DisplayName("應成功產生 Access Token")
        void shouldGenerateAccessToken() {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            user.activate();
            user.assignRole("ADMIN");
            user.assignRole("USER");

            // When
            String token = jwtService.generateAccessToken(user);

            // Then
            assertNotNull(token);
            assertTrue(token.split("\\.").length == 3); // JWT format: header.payload.signature
        }

        @Test
        @DisplayName("產生的 Token 應包含正確的 claims")
        void shouldContainCorrectClaims() {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            user.activate();
            user.assignRole("ADMIN");

            // When
            String token = jwtService.generateAccessToken(user);
            Map<String, Object> claims = jwtService.extractAllClaims(token);

            // Then
            assertEquals(user.getId().getValue(), claims.get("sub"));
            assertEquals(user.getUsername(), claims.get("username"));
            assertEquals(user.getEmail().getValue(), claims.get("email"));
            assertNotNull(claims.get("roles"));
            assertTrue(((List<?>) claims.get("roles")).contains("ADMIN"));
        }
    }

    @Nested
    @DisplayName("Refresh Token 產生")
    class GenerateRefreshTokenTests {

        @Test
        @DisplayName("應成功產生 Refresh Token")
        void shouldGenerateRefreshToken() {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            user.activate();

            // When
            String token = jwtService.generateRefreshToken(user);

            // Then
            assertNotNull(token);
            assertTrue(token.split("\\.").length == 3);
        }

        @Test
        @DisplayName("Refresh Token 應有較長的有效期")
        void shouldHaveLongerExpiry() {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            user.activate();

            // When
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            long accessTokenExpiry = jwtService.getExpirationTime(accessToken);
            long refreshTokenExpiry = jwtService.getExpirationTime(refreshToken);

            // Then
            assertTrue(refreshTokenExpiry > accessTokenExpiry);
        }
    }

    @Nested
    @DisplayName("Token 驗證")
    class ValidateTokenTests {

        @Test
        @DisplayName("有效 Token 應驗證成功")
        void shouldValidateValidToken() {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            user.activate();
            String token = jwtService.generateAccessToken(user);

            // When
            boolean isValid = jwtService.validateToken(token);

            // Then
            assertTrue(isValid);
        }

        @Test
        @DisplayName("無效 Token 應驗證失敗")
        void shouldRejectInvalidToken() {
            // When
            boolean isValid = jwtService.validateToken("invalid.token.here");

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("空 Token 應驗證失敗")
        void shouldRejectNullToken() {
            // When
            boolean isValid = jwtService.validateToken(null);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("被竄改的 Token 應驗證失敗")
        void shouldRejectTamperedToken() {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            user.activate();
            String token = jwtService.generateAccessToken(user);

            // Tamper with the token
            String[] parts = token.split("\\.");
            String tamperedToken = parts[0] + "." + parts[1] + "x" + "." + parts[2];

            // When
            boolean isValid = jwtService.validateToken(tamperedToken);

            // Then
            assertFalse(isValid);
        }
    }

    @Nested
    @DisplayName("Token Claims 擷取")
    class ExtractClaimsTests {

        @Test
        @DisplayName("應成功擷取使用者 ID")
        void shouldExtractUserId() {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            user.activate();
            String token = jwtService.generateAccessToken(user);

            // When
            String userId = jwtService.extractUserId(token);

            // Then
            assertEquals(user.getId().getValue(), userId);
        }

        @Test
        @DisplayName("應成功擷取使用者名稱")
        void shouldExtractUsername() {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            user.activate();
            String token = jwtService.generateAccessToken(user);

            // When
            String username = jwtService.extractUsername(token);

            // Then
            assertEquals("john.doe", username);
        }

        @Test
        @DisplayName("應成功擷取角色列表")
        void shouldExtractRoles() {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            user.activate();
            user.assignRole("ADMIN");
            user.assignRole("USER");
            String token = jwtService.generateAccessToken(user);

            // When
            List<String> roles = jwtService.extractRoles(token);

            // Then
            assertNotNull(roles);
            assertEquals(2, roles.size());
            assertTrue(roles.contains("ADMIN"));
            assertTrue(roles.contains("USER"));
        }
    }

    @Nested
    @DisplayName("Token 有效期")
    class TokenExpirationTests {

        @Test
        @DisplayName("應正確判斷 Token 是否過期")
        void shouldCheckTokenExpiration() {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            user.activate();
            String token = jwtService.generateAccessToken(user);

            // When
            boolean isExpired = jwtService.isTokenExpired(token);

            // Then
            assertFalse(isExpired); // 剛產生的 Token 不應該過期
        }
    }
}
