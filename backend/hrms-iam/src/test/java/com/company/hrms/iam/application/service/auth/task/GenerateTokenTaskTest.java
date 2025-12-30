package com.company.hrms.iam.application.service.auth.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.service.JwtTokenDomainService;

/**
 * GenerateTokenTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GenerateTokenTask 測試")
class GenerateTokenTaskTest {

    @Mock
    private JwtTokenDomainService jwtTokenService;

    @InjectMocks
    private GenerateTokenTask task;

    private AuthContext context;
    private User testUser;

    @BeforeEach
    void setUp() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("john.doe")
                .password("Password1!")
                .build();
        context = new AuthContext(loginRequest);

        testUser = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
        testUser.activate();
        context.setUser(testUser);
    }

    @Test
    @DisplayName("應成功產生 JWT Token 並設置到 Context")
    void shouldGenerateTokensSuccessfully() throws Exception {
        // Given
        when(jwtTokenService.generateAccessToken(any(User.class))).thenReturn("test-access-token");
        when(jwtTokenService.generateRefreshToken(any(User.class))).thenReturn("test-refresh-token");

        // When
        task.execute(context);

        // Then
        assertEquals("test-access-token", context.getAccessToken());
        assertEquals("test-refresh-token", context.getRefreshToken());
    }

    @Test
    @DisplayName("getName 應返回 '產生 JWT Token'")
    void shouldReturnCorrectName() {
        assertEquals("產生 JWT Token", task.getName());
    }
}
