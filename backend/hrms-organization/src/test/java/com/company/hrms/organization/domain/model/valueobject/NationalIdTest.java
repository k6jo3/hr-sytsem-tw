package com.company.hrms.organization.domain.model.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.company.hrms.common.exception.DomainException;

/**
 * NationalId 值物件單元測試
 * 測試台灣身分證字號驗證邏輯
 */
@DisplayName("NationalId 值物件測試")
class NationalIdTest {

    @Nested
    @DisplayName("有效身分證字號")
    class ValidNationalIdTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "A123456789",  // 台北市男性
                "B223456781",  // 台中市女性
                "C123456782",  // 基隆市男性
                "D123456783",  // 台南市男性
                "E123456784"   // 高雄市男性
        })
        @DisplayName("應接受有效的身分證字號")
        void shouldAcceptValidNationalId(String nationalId) {
            // When
            NationalId id = new NationalId(nationalId);

            // Then
            assertNotNull(id);
            assertEquals(nationalId, id.getValue());
        }

        @Test
        @DisplayName("應將小寫字母轉為大寫")
        void shouldConvertToUpperCase() {
            // When
            NationalId id = new NationalId("a123456789");

            // Then
            assertEquals("A123456789", id.getValue());
        }

        @Test
        @DisplayName("應去除前後空白")
        void shouldTrimWhitespace() {
            // When
            NationalId id = new NationalId("  A123456789  ");

            // Then
            assertEquals("A123456789", id.getValue());
        }
    }

    @Nested
    @DisplayName("無效身分證字號")
    class InvalidNationalIdTests {

        @Test
        @DisplayName("身分證字號為 null 時應拋出例外")
        void shouldThrowExceptionWhenNull() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> new NationalId(null));
            assertEquals("NATIONAL_ID_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("身分證字號為空白時應拋出例外")
        void shouldThrowExceptionWhenBlank() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> new NationalId("   "));
            assertEquals("NATIONAL_ID_REQUIRED", exception.getErrorCode());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "1234567890",   // 沒有字母開頭
                "A12345678",    // 長度不足
                "A12345678901", // 長度過長
                "AB23456789",   // 第二位不是 1 或 2
                "A323456789"    // 第二位不是 1 或 2
        })
        @DisplayName("應拒絕格式無效的身分證字號")
        void shouldRejectInvalidFormat(String invalidId) {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> new NationalId(invalidId));
            assertEquals("NATIONAL_ID_FORMAT_INVALID", exception.getErrorCode());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "A123456788",  // 驗證碼錯誤
                "B223456780",  // 驗證碼錯誤
                "C123456781"   // 驗證碼錯誤
        })
        @DisplayName("應拒絕驗證碼錯誤的身分證字號")
        void shouldRejectInvalidChecksum(String invalidId) {
            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> new NationalId(invalidId));
            assertEquals("NATIONAL_ID_CHECKSUM_INVALID", exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("遮罩功能")
    class MaskingTests {

        @Test
        @DisplayName("應正確遮罩身分證字號")
        void shouldMaskNationalId() {
            // Given
            NationalId id = new NationalId("A123456789");

            // When
            String masked = id.getMaskedValue();

            // Then
            assertEquals("A12***6789", masked);
        }

        @Test
        @DisplayName("toString 應回傳遮罩後的值")
        void toStringShouldReturnMaskedValue() {
            // Given
            NationalId id = new NationalId("A123456789");

            // Then
            assertEquals("A12***6789", id.toString());
        }
    }

    @Nested
    @DisplayName("相等性")
    class EqualityTests {

        @Test
        @DisplayName("相同身分證字號應視為相等")
        void shouldBeEqualForSameNationalId() {
            // Given
            NationalId id1 = new NationalId("A123456789");
            NationalId id2 = new NationalId("A123456789");

            // Then
            assertEquals(id1, id2);
            assertEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("不區分大小寫應視為相等")
        void shouldBeEqualCaseInsensitive() {
            // Given
            NationalId id1 = new NationalId("a123456789");
            NationalId id2 = new NationalId("A123456789");

            // Then
            assertEquals(id1, id2);
        }
    }
}
