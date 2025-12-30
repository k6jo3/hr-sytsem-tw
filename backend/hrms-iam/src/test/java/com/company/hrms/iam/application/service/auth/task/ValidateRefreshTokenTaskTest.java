package com.company.hrms.iam.application.service.auth.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.api.request.auth.RefreshTokenRequest;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.service.JwtTokenDomainService;

/**
 * ValidateRefreshTokenTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValidateRefreshTokenTask 測試")
class ValidateRefreshTokenTaskTest {

    @Mock
    private JwtTokenDomainService jwtTokenService;

    @InjectMocks
    private ValidateRefreshTokenTask task;

    private AuthContext context;

    @BeforeEach
    void setUp() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid-refresh-token")
                .build();
        context = new AuthContext();
        context.setRefreshTokenRequest(request);
    }

    @Nested
    @DisplayName("驗證成功")
    class SuccessTests {

        @Test
        @DisplayName("有效的 Refresh Token 應通過驗證")
        void shouldPassWhenTokenValid() throws Exception {
            // Given
            when(jwtTokenService.validateToken("valid-refresh-token")).thenReturn(true);
            when(jwtTokenService.extractUserId("valid-refresh-token")).thenReturn("user-123");

            // When
            task.execute(context);

            // Then
            assertEquals("user-123", context.getUserId());
        }
    }

    @Nested
    @DisplayName("驗證失敗")
    class FailureTests {

        @Test
        @DisplayName("無效的 Refresh Token 應拋出 INVALID_REFRESH_TOKEN 例外")
        void shouldThrowExceptionWhenTokenInvalid() {
            // Given
            when(jwtTokenService.validateToken("valid-refresh-token")).thenReturn(false);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("INVALID_REFRESH_TOKEN", exception.getErrorCode());
        }
    }

    @Test
    @DisplayName("getName 應返回 '驗證 Refresh Token'")
    void shouldReturnCorrectName() {
        assertEquals("驗證 Refresh Token", task.getName());
    }
}
