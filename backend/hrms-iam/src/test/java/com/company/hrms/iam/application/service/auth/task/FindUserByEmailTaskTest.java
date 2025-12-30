package com.company.hrms.iam.application.service.auth.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.iam.api.request.auth.ForgotPasswordRequest;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.repository.IUserRepository;

/**
 * FindUserByEmailTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FindUserByEmailTask 測試")
class FindUserByEmailTaskTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private FindUserByEmailTask task;

    private AuthContext context;
    private ForgotPasswordRequest request;

    @BeforeEach
    void setUp() {
        request = new ForgotPasswordRequest();
        request.setEmail("john@example.com");

        context = new AuthContext();
        context.setForgotPasswordRequest(request);
    }

    @Nested
    @DisplayName("查找成功")
    class SuccessTests {

        @Test
        @DisplayName("Email 存在且使用者有效應設置使用者到 Context")
        void shouldSetUserWhenFoundAndActive() throws Exception {
            // Given
            User mockUser = mock(User.class);
            when(mockUser.getUsername()).thenReturn("john.doe");
            when(mockUser.isActive()).thenReturn(true);
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(mockUser));

            // When
            task.execute(context);

            // Then
            assertEquals(mockUser, context.getUser());
        }
    }

    @Nested
    @DisplayName("查找失敗 (安全模式)")
    class FailureTests {

        @Test
        @DisplayName("Email 不存在不應拋出例外且不設置使用者")
        void shouldNotThrowExceptionWhenNotFound() throws Exception {
            // Given
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

            // When
            task.execute(context);

            // Then
            assertNull(context.getUser());
        }

        @Test
        @DisplayName("使用者已停用不應拋出例外且不設置使用者")
        void shouldNotThrowExceptionWhenUserInactive() throws Exception {
            // Given
            User mockUser = mock(User.class);
            when(mockUser.isActive()).thenReturn(false);
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(mockUser));

            // When
            task.execute(context);

            // Then
            assertNull(context.getUser());
        }
    }

    @Test
    @DisplayName("getName 應返回 '查找使用者 by Email'")
    void shouldReturnCorrectName() {
        assertEquals("查找使用者 by Email", task.getName());
    }
}
