package com.company.hrms.organization.domain.model.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.company.hrms.common.exception.DomainException;

/**
 * Email 值物件單元測試
 */
@DisplayName("Email 值物件測試")
class EmailTest {

    @Nested
    @DisplayName("有效 Email")
    class ValidEmailTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "test@example.com",
                "user.name@domain.com",
                "user+tag@example.org",
                "user_name@sub.domain.com",
                "TEST@EXAMPLE.COM"
        })
        @DisplayName("應接受有效的 Email 格式")
        void shouldAcceptValidEmail(String email) {
            // When
            Email emailVO = new Email(email);

            // Then
            assertNotNull(emailVO);
            assertEquals(email.toLowerCase().trim(), emailVO.getValue());
        }

        @Test
        @DisplayName("應將 Email 轉為小寫")
        void shouldConvertToLowerCase() {
            // When
            Email email = new Email("TEST@EXAMPLE.COM");

            // Then
            assertEquals("test@example.com", email.getValue());
        }

        @Test
        @DisplayName("應去除前後空白")
        void shouldTrimWhitespace() {
            // When
            Email email = new Email("  test@example.com  ");

            // Then
            assertEquals("test@example.com", email.getValue());
        }
    }

    @Nested
    @DisplayName("無效 Email")
    class InvalidEmailTests {

        @Test
        @DisplayName("Email 為 null 時應拋出例外")
        void shouldThrowExceptionWhenNull() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> new Email(null));
            assertEquals("EMAIL_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("Email 為空白時應拋出例外")
        void shouldThrowExceptionWhenBlank() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> new Email("   "));
            assertEquals("EMAIL_REQUIRED", exception.getErrorCode());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "invalid",
                "invalid@",
                "@example.com",
                "test@",
                "test.example.com"
        })
        @DisplayName("應拒絕無效的 Email 格式")
        void shouldRejectInvalidEmail(String invalidEmail) {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> new Email(invalidEmail));
            assertEquals("EMAIL_INVALID", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Email 部分解析")
    class EmailParsingTests {

        @Test
        @DisplayName("應正確取得域名部分")
        void shouldGetDomain() {
            // Given
            Email email = new Email("user@example.com");

            // Then
            assertEquals("example.com", email.getDomain());
        }

        @Test
        @DisplayName("應正確取得使用者名稱部分")
        void shouldGetUsername() {
            // Given
            Email email = new Email("user@example.com");

            // Then
            assertEquals("user", email.getUsername());
        }
    }

    @Nested
    @DisplayName("相等性")
    class EqualityTests {

        @Test
        @DisplayName("相同 Email 應視為相等")
        void shouldBeEqualForSameEmail() {
            // Given
            Email email1 = new Email("test@example.com");
            Email email2 = new Email("test@example.com");

            // Then
            assertEquals(email1, email2);
            assertEquals(email1.hashCode(), email2.hashCode());
        }

        @Test
        @DisplayName("不區分大小寫應視為相等")
        void shouldBeEqualCaseInsensitive() {
            // Given
            Email email1 = new Email("TEST@EXAMPLE.COM");
            Email email2 = new Email("test@example.com");

            // Then
            assertEquals(email1, email2);
        }
    }
}
