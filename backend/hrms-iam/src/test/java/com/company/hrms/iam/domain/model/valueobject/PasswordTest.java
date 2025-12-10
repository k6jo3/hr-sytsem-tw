package com.company.hrms.iam.domain.model.valueobject;

import com.company.hrms.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Password 值物件單元測試
 * 測試密碼強度驗證邏輯
 */
@DisplayName("Password 值物件測試")
class PasswordTest {

    @Nested
    @DisplayName("有效密碼")
    class ValidPasswordTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "Password1!",
            "Abcd1234@",
            "MyP@ssw0rd",
            "Str0ng#Pass",
            "Test1234!"
        })
        @DisplayName("應接受符合強度要求的密碼")
        void shouldAcceptValidPassword(String password) {
            // When
            Password passwordVO = Password.of(password);

            // Then
            assertNotNull(passwordVO);
            assertEquals(password, passwordVO.getValue());
        }

        @Test
        @DisplayName("應正確驗證密碼強度")
        void shouldValidatePasswordStrength() {
            // Given
            String validPassword = "MyP@ssw0rd";

            // When
            Password password = Password.of(validPassword);

            // Then
            assertTrue(password.isStrong());
        }
    }

    @Nested
    @DisplayName("無效密碼 - 長度不足")
    class InvalidPasswordLengthTests {

        @Test
        @DisplayName("密碼少於 8 字元應拋出例外")
        void shouldRejectShortPassword() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Password.of("Abc1!"));
            assertEquals("PASSWORD_TOO_SHORT", exception.getErrorCode());
        }

        @Test
        @DisplayName("密碼為空時應拋出例外")
        void shouldRejectNullPassword() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Password.of(null));
            assertEquals("PASSWORD_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("密碼為空白時應拋出例外")
        void shouldRejectBlankPassword() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Password.of("   "));
            assertEquals("PASSWORD_REQUIRED", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("無效密碼 - 缺少必要字元")
    class InvalidPasswordCharacterTests {

        @Test
        @DisplayName("缺少大寫字母應拋出例外")
        void shouldRejectPasswordWithoutUppercase() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Password.of("password1!"));
            assertEquals("PASSWORD_WEAK", exception.getErrorCode());
            assertTrue(exception.getMessage().contains("大寫"));
        }

        @Test
        @DisplayName("缺少小寫字母應拋出例外")
        void shouldRejectPasswordWithoutLowercase() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Password.of("PASSWORD1!"));
            assertEquals("PASSWORD_WEAK", exception.getErrorCode());
            assertTrue(exception.getMessage().contains("小寫"));
        }

        @Test
        @DisplayName("缺少數字應拋出例外")
        void shouldRejectPasswordWithoutDigit() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Password.of("Password!"));
            assertEquals("PASSWORD_WEAK", exception.getErrorCode());
            assertTrue(exception.getMessage().contains("數字"));
        }

        @Test
        @DisplayName("缺少特殊字元應拋出例外")
        void shouldRejectPasswordWithoutSpecialChar() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> Password.of("Password1"));
            assertEquals("PASSWORD_WEAK", exception.getErrorCode());
            assertTrue(exception.getMessage().contains("特殊字元"));
        }
    }

    @Nested
    @DisplayName("密碼比對")
    class PasswordMatchTests {

        @Test
        @DisplayName("相同密碼應視為相等")
        void shouldMatchSamePassword() {
            // Given
            Password password1 = Password.of("MyP@ssw0rd");
            Password password2 = Password.of("MyP@ssw0rd");

            // Then
            assertEquals(password1, password2);
            assertEquals(password1.hashCode(), password2.hashCode());
        }

        @Test
        @DisplayName("不同密碼應視為不相等")
        void shouldNotMatchDifferentPassword() {
            // Given
            Password password1 = Password.of("MyP@ssw0rd");
            Password password2 = Password.of("OtherP@ss1");

            // Then
            assertNotEquals(password1, password2);
        }
    }
}
