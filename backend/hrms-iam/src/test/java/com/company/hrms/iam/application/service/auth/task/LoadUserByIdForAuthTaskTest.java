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

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;

/**
 * LoadUserByIdForAuthTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoadUserByIdForAuthTask 測試")
class LoadUserByIdForAuthTaskTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private LoadUserByIdForAuthTask task;

    private AuthContext context;

    @BeforeEach
    void setUp() {
        context = new AuthContext();
        context.setUserId("user-123");
    }

    @Nested
    @DisplayName("載入成功")
    class SuccessTests {

        @Test
        @DisplayName("使用者存在且有效應成功載入")
        void shouldLoadUserSuccessfully() throws Exception {
            // Given
            User mockUser = mock(User.class);
            when(mockUser.isActive()).thenReturn(true);
            when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(mockUser));

            // When
            task.execute(context);

            // Then
            assertEquals(mockUser, context.getUser());
        }
    }

    @Nested
    @DisplayName("載入失敗")
    class FailureTests {

        @Test
        @DisplayName("使用者不存在應拋出 USER_NOT_FOUND 例外")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        }

        @Test
        @DisplayName("使用者已停用應拋出 USER_INACTIVE 例外")
        void shouldThrowExceptionWhenUserInactive() {
            // Given
            User mockUser = mock(User.class);
            when(mockUser.isActive()).thenReturn(false);
            when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(mockUser));

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("USER_INACTIVE", exception.getErrorCode());
        }
    }

    @Test
    @DisplayName("getName 應返回 '載入使用者'")
    void shouldReturnCorrectName() {
        assertEquals("載入使用者", task.getName());
    }
}
