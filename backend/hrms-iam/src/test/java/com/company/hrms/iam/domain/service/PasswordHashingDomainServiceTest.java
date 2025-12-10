package com.company.hrms.iam.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PasswordHashingDomainService 單元測試
 * 測試密碼雜湊與驗證邏輯
 */
@DisplayName("PasswordHashingDomainService 測試")
class PasswordHashingDomainServiceTest {

    private PasswordHashingDomainService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordHashingDomainService();
    }

    @Nested
    @DisplayName("密碼雜湊")
    class HashPasswordTests {

        @Test
        @DisplayName("應成功雜湊密碼")
        void shouldHashPassword() {
            // Given
            String rawPassword = "MyP@ssw0rd";

            // When
            String hashedPassword = passwordService.hash(rawPassword);

            // Then
            assertNotNull(hashedPassword);
            assertNotEquals(rawPassword, hashedPassword);
            assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$"));
        }

        @Test
        @DisplayName("相同密碼應產生不同雜湊值 (因為 salt)")
        void shouldProduceDifferentHashForSamePassword() {
            // Given
            String rawPassword = "MyP@ssw0rd";

            // When
            String hash1 = passwordService.hash(rawPassword);
            String hash2 = passwordService.hash(rawPassword);

            // Then
            assertNotEquals(hash1, hash2);
        }
    }

    @Nested
    @DisplayName("密碼驗證")
    class VerifyPasswordTests {

        @Test
        @DisplayName("正確密碼應驗證成功")
        void shouldVerifyCorrectPassword() {
            // Given
            String rawPassword = "MyP@ssw0rd";
            String hashedPassword = passwordService.hash(rawPassword);

            // When
            boolean result = passwordService.verify(rawPassword, hashedPassword);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("錯誤密碼應驗證失敗")
        void shouldRejectIncorrectPassword() {
            // Given
            String rawPassword = "MyP@ssw0rd";
            String wrongPassword = "WrongP@ss1";
            String hashedPassword = passwordService.hash(rawPassword);

            // When
            boolean result = passwordService.verify(wrongPassword, hashedPassword);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("空密碼應驗證失敗")
        void shouldRejectNullPassword() {
            // Given
            String hashedPassword = passwordService.hash("MyP@ssw0rd");

            // When
            boolean result = passwordService.verify(null, hashedPassword);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("空雜湊值應驗證失敗")
        void shouldRejectNullHash() {
            // When
            boolean result = passwordService.verify("MyP@ssw0rd", null);

            // Then
            assertFalse(result);
        }
    }
}
