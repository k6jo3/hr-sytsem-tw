package com.company.hrms.iam.application.service.auth;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.api.response.auth.LoginResponse;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.AccountLockingDomainService;
import com.company.hrms.iam.domain.service.JwtTokenDomainService;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * LoginServiceImpl 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginServiceImpl 測試")
class LoginServiceImplTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordHashingDomainService passwordHashingService;

    @Mock
    private JwtTokenDomainService jwtTokenService;

    @Mock
    private AccountLockingDomainService accountLockingService;

    @InjectMocks
    private LoginServiceImpl loginService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // 建立測試用使用者
        testUser = User.create("john.doe", "john@example.com",
                "hashedPassword", "John Doe");
        testUser.activate();
        testUser.assignRole("USER");

        // 建立登入請求
        loginRequest = LoginRequest.builder()
                .username("john.doe")
                .password("Password1!")
                .build();
    }

    @Nested
    @DisplayName("登入成功")
    class LoginSuccessTests {

        @Test
        @DisplayName("應成功登入並返回 Token")
        void shouldLoginSuccessfully() throws Exception {
            // Given
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
            when(passwordHashingService.verify("Password1!", "hashedPassword")).thenReturn(true);
            when(jwtTokenService.generateAccessToken(any(User.class))).thenReturn("access-token");
            when(jwtTokenService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

            // When
            LoginResponse response = loginService.execCommand(loginRequest, null);

            // Then
            assertNotNull(response);
            assertEquals("access-token", response.getAccessToken());
            assertEquals("refresh-token", response.getRefreshToken());
            assertEquals("Bearer", response.getTokenType());
            assertNotNull(response.getUser());
            assertEquals("john.doe", response.getUser().getUsername());
            assertEquals("John Doe", response.getUser().getDisplayName());

            // 驗證登入成功後記錄
            verify(userRepository).update(any(User.class));
        }

        @Test
        @DisplayName("登入成功應重置失敗次數")
        void shouldResetFailedAttemptsOnSuccess() throws Exception {
            // Given
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
            when(passwordHashingService.verify(anyString(), anyString())).thenReturn(true);
            when(jwtTokenService.generateAccessToken(any())).thenReturn("access-token");
            when(jwtTokenService.generateRefreshToken(any())).thenReturn("refresh-token");

            // When
            loginService.execCommand(loginRequest, null);

            // Then
            assertEquals(0, testUser.getFailedLoginAttempts());
            assertNotNull(testUser.getLastLoginAt());
        }
    }

    @Nested
    @DisplayName("登入失敗")
    class LoginFailureTests {

        @Test
        @DisplayName("使用者不存在應拋出例外")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> loginService.execCommand(loginRequest, null));
            assertEquals("LOGIN_FAILED", exception.getErrorCode());
        }

        @Test
        @DisplayName("密碼錯誤應拋出例外並記錄失敗")
        void shouldThrowExceptionWhenPasswordIncorrect() {
            // Given
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
            when(passwordHashingService.verify(anyString(), anyString())).thenReturn(false);
            when(accountLockingService.recordFailureAndCheckLock(any())).thenReturn(false);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> loginService.execCommand(loginRequest, null));
            assertEquals("LOGIN_FAILED", exception.getErrorCode());

            // 驗證記錄失敗
            verify(accountLockingService).recordFailureAndCheckLock(testUser);
            verify(userRepository).update(testUser);
        }

        @Test
        @DisplayName("帳號已停用應拋出例外")
        void shouldThrowExceptionWhenUserInactive() {
            // Given
            testUser.deactivate();
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> loginService.execCommand(loginRequest, null));
            assertEquals("USER_INACTIVE", exception.getErrorCode());
        }

        @Test
        @DisplayName("帳號已鎖定應拋出例外")
        void shouldThrowExceptionWhenUserLocked() {
            // Given
            testUser.lock(LocalDateTime.now().plusMinutes(30));
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
            when(accountLockingService.checkAndUnlock(testUser)).thenReturn(false);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> loginService.execCommand(loginRequest, null));
            assertEquals("USER_LOCKED", exception.getErrorCode());
        }

        @Test
        @DisplayName("鎖定時間已過應自動解鎖並允許登入")
        void shouldAutoUnlockWhenLockExpired() throws Exception {
            // Given
            testUser.lock(LocalDateTime.now().minusMinutes(1)); // 鎖定時間已過
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
            when(accountLockingService.checkAndUnlock(testUser)).thenAnswer(inv -> {
                testUser.unlock();
                return true;
            });
            when(passwordHashingService.verify(anyString(), anyString())).thenReturn(true);
            when(jwtTokenService.generateAccessToken(any())).thenReturn("access-token");
            when(jwtTokenService.generateRefreshToken(any())).thenReturn("refresh-token");

            // When
            LoginResponse response = loginService.execCommand(loginRequest, null);

            // Then
            assertNotNull(response);
            verify(accountLockingService).checkAndUnlock(testUser);
        }

        @Test
        @DisplayName("連續失敗 5 次應鎖定帳號")
        void shouldLockAccountAfterFiveFailedAttempts() {
            // Given
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
            when(passwordHashingService.verify(anyString(), anyString())).thenReturn(false);
            when(accountLockingService.recordFailureAndCheckLock(any())).thenReturn(true);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> loginService.execCommand(loginRequest, null));
            assertEquals("USER_LOCKED", exception.getErrorCode());
            assertTrue(exception.getMessage().contains("鎖定"));
        }
    }
}
