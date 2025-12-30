package com.company.hrms.iam.application.service.auth.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.service.EmailDomainService;
import com.company.hrms.iam.domain.service.PasswordResetTokenDomainService;

/**
 * GenerateAndSendResetEmailTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GenerateAndSendResetEmailTask 測試")
class GenerateAndSendResetEmailTaskTest {

    @Mock
    private PasswordResetTokenDomainService passwordResetTokenService;

    @Mock
    private EmailDomainService emailService;

    @InjectMocks
    private GenerateAndSendResetEmailTask task;

    private AuthContext context;
    private User mockUser;

    @BeforeEach
    void setUp() {
        context = new AuthContext();

        mockUser = mock(User.class);
        // Stubbings moved to individual tests
    }

    @Nested
    @DisplayName("執行成功")
    class SuccessTests {

        @Test
        @DisplayName("應產生 Token 並發送郵件")
        void shouldGenerateTokenAndSendEmail() throws Exception {
            // Given
            String token = "reset-token";

            when(mockUser.getId()).thenReturn(new UserId("user-123"));
            when(mockUser.getEmail()).thenReturn(new Email("john@example.com"));
            when(mockUser.getDisplayName()).thenReturn("John Doe");
            when(mockUser.getUsername()).thenReturn("john.doe");
            context.setUser(mockUser);

            when(passwordResetTokenService.generateToken("user-123")).thenReturn(token);

            // When
            task.execute(context);

            // Then
            verify(passwordResetTokenService).generateToken("user-123");
            verify(emailService).sendPasswordResetEmail("john@example.com", token, "John Doe");
            assertEquals(token, context.getResetToken());
        }
    }

    @Nested
    @DisplayName("執行失敗")
    class FailureTests {

        @Test
        @DisplayName("發送郵件異常時應捕獲並記錄，不拋出例外")
        void shouldCatchExceptionWhenSendingFails() throws Exception {
            // Given
            when(mockUser.getId()).thenReturn(new UserId("user-123"));
            when(mockUser.getEmail()).thenReturn(new Email("john@example.com"));
            when(mockUser.getDisplayName()).thenReturn("John Doe");
            // Note: getUsername() is not stubbed because it's only called in success path (log.info)
            context.setUser(mockUser);

            when(passwordResetTokenService.generateToken("user-123")).thenReturn("token");
            doThrow(new RuntimeException("Email service down"))
                    .when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());

            // When & Then
            assertDoesNotThrow(() -> task.execute(context));
            verify(passwordResetTokenService).generateToken("user-123");
            verify(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
        }
    }

    @Test
    @DisplayName("shouldExecute 在有使用者時應返回 true")
    void shouldExecuteWhenUserExists() {
        context.setUser(mockUser);
        assertTrue(task.shouldExecute(context));
    }

    @Test
    @DisplayName("shouldExecute 在無使用者時應返回 false")
    void shouldNotExecuteWhenUserNotExists() {
        context.setUser(null);
        assertFalse(task.shouldExecute(context));
    }

    @Test
    @DisplayName("getName 應返回 '產生並發送密碼重置郵件'")
    void shouldReturnCorrectName() {
        assertEquals("產生並發送密碼重置郵件", task.getName());
    }
}
