package com.company.hrms.reporting.domain.model.dashboard;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * WidgetPosition 值物件單元測試
 * <p>
 * 測試 Widget 位置驗證邏輯：座標、大小、Grid 邊界
 * </p>
 *
 * @author Claude
 * @since 2026-03-19
 */
@DisplayName("WidgetPosition 值物件測試")
class WidgetPositionTest {

    @Nested
    @DisplayName("validate - 位置驗證")
    class ValidateTests {

        @Test
        @DisplayName("合法位置應通過驗證")
        void validPosition_shouldPass() {
            // Given
            WidgetPosition pos = new WidgetPosition(0, 0, 3, 2);

            // When & Then
            assertThatCode(pos::validate).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("X 座標為負數應拋出例外")
        void negativeX_shouldThrow() {
            // Given
            WidgetPosition pos = new WidgetPosition(-1, 0, 3, 2);

            // Then
            assertThatThrownBy(pos::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("X 座標不可為負數");
        }

        @Test
        @DisplayName("Y 座標為負數應拋出例外")
        void negativeY_shouldThrow() {
            // Given
            WidgetPosition pos = new WidgetPosition(0, -1, 3, 2);

            // Then
            assertThatThrownBy(pos::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Y 座標不可為負數");
        }

        @Test
        @DisplayName("寬度為 0 應拋出例外")
        void zeroWidth_shouldThrow() {
            // Given
            WidgetPosition pos = new WidgetPosition(0, 0, 0, 2);

            // Then
            assertThatThrownBy(pos::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("寬度必須大於 0");
        }

        @Test
        @DisplayName("高度為 0 應拋出例外")
        void zeroHeight_shouldThrow() {
            // Given
            WidgetPosition pos = new WidgetPosition(0, 0, 3, 0);

            // Then
            assertThatThrownBy(pos::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("高度必須大於 0");
        }

        @Test
        @DisplayName("超出 Grid 範圍（x + w > 12）應拋出例外")
        void exceedGridRange_shouldThrow() {
            // Given
            WidgetPosition pos = new WidgetPosition(10, 0, 4, 2);

            // Then
            assertThatThrownBy(pos::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("超出 Grid 範圍");
        }

        @Test
        @DisplayName("剛好在 Grid 邊界（x + w = 12）應通過")
        void exactGridBoundary_shouldPass() {
            // Given
            WidgetPosition pos = new WidgetPosition(9, 0, 3, 2);

            // When & Then
            assertThatCode(pos::validate).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("equals 與 hashCode 測試")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("相同座標和大小應相等")
        void sameValues_shouldBeEqual() {
            // Given
            WidgetPosition pos1 = new WidgetPosition(0, 0, 3, 2);
            WidgetPosition pos2 = new WidgetPosition(0, 0, 3, 2);

            // Then
            assertThat(pos1).isEqualTo(pos2);
            assertThat(pos1.hashCode()).isEqualTo(pos2.hashCode());
        }

        @Test
        @DisplayName("不同座標應不相等")
        void differentValues_shouldNotBeEqual() {
            // Given
            WidgetPosition pos1 = new WidgetPosition(0, 0, 3, 2);
            WidgetPosition pos2 = new WidgetPosition(1, 0, 3, 2);

            // Then
            assertThat(pos1).isNotEqualTo(pos2);
        }
    }

    @Nested
    @DisplayName("toString 測試")
    class ToStringTests {

        @Test
        @DisplayName("應包含座標與大小資訊")
        void shouldContainPositionInfo() {
            // Given
            WidgetPosition pos = new WidgetPosition(2, 3, 4, 5);

            // Then
            assertThat(pos.toString()).contains("x=2").contains("y=3").contains("w=4").contains("h=5");
        }
    }
}
