package com.company.hrms.notification.domain.model.valueobject;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * QuietHours 值物件單元測試
 * <p>
 * 測試靜音時段邏輯：建立、跨日判斷、邊界值
 * </p>
 *
 * @author Claude
 * @since 2026-03-19
 */
@DisplayName("QuietHours 值物件測試")
class QuietHoursTest {

    @Nested
    @DisplayName("工廠方法測試")
    class FactoryMethodTests {

        @Test
        @DisplayName("建立預設靜音時段 - 22:00 至 08:00")
        void defaultQuietHours_shouldBe22To08() {
            // When
            QuietHours qh = QuietHours.defaultQuietHours();

            // Then
            assertThat(qh.isEnabled()).isTrue();
            assertThat(qh.getStartTime()).isEqualTo(LocalTime.of(22, 0));
            assertThat(qh.getEndTime()).isEqualTo(LocalTime.of(8, 0));
        }

        @Test
        @DisplayName("建立停用的靜音時段")
        void disabled_shouldBeDisabled() {
            // When
            QuietHours qh = QuietHours.disabled();

            // Then
            assertThat(qh.isEnabled()).isFalse();
            assertThat(qh.getStartTime()).isNull();
            assertThat(qh.getEndTime()).isNull();
        }

        @Test
        @DisplayName("建立自訂靜音時段")
        void of_shouldCreateCustomQuietHours() {
            // When
            QuietHours qh = QuietHours.of(LocalTime.of(23, 30), LocalTime.of(6, 0));

            // Then
            assertThat(qh.isEnabled()).isTrue();
            assertThat(qh.getStartTime()).isEqualTo(LocalTime.of(23, 30));
            assertThat(qh.getEndTime()).isEqualTo(LocalTime.of(6, 0));
        }

        @Test
        @DisplayName("啟用時 startTime 為 null 應拋出例外")
        void of_shouldThrowWhenStartTimeIsNull() {
            assertThatThrownBy(() -> QuietHours.of(null, LocalTime.of(8, 0)))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("開始時間");
        }

        @Test
        @DisplayName("啟用時 endTime 為 null 應拋出例外")
        void of_shouldThrowWhenEndTimeIsNull() {
            assertThatThrownBy(() -> QuietHours.of(LocalTime.of(22, 0), null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("結束時間");
        }
    }

    @Nested
    @DisplayName("isInQuietHours - 靜音時段判斷")
    class IsInQuietHoursTests {

        @Test
        @DisplayName("停用狀態下，任何時間都不在靜音時段")
        void disabled_shouldAlwaysReturnFalse() {
            // Given
            QuietHours qh = QuietHours.disabled();

            // When & Then
            assertThat(qh.isInQuietHours(LocalTime.of(23, 0))).isFalse();
            assertThat(qh.isInQuietHours(LocalTime.of(3, 0))).isFalse();
            assertThat(qh.isInQuietHours(LocalTime.of(12, 0))).isFalse();
        }

        @Test
        @DisplayName("time 為 null 時應回傳 false")
        void shouldReturnFalseWhenTimeIsNull() {
            // Given
            QuietHours qh = QuietHours.defaultQuietHours();

            // When & Then
            assertThat(qh.isInQuietHours(null)).isFalse();
        }

        @Test
        @DisplayName("跨日靜音（22:00-08:00）- 23:00 應在靜音時段內")
        void crossDay_23_shouldBeInQuietHours() {
            // Given
            QuietHours qh = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));

            // Then
            assertThat(qh.isInQuietHours(LocalTime.of(23, 0))).isTrue();
        }

        @Test
        @DisplayName("跨日靜音（22:00-08:00）- 03:00 應在靜音時段內")
        void crossDay_03_shouldBeInQuietHours() {
            // Given
            QuietHours qh = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));

            // Then
            assertThat(qh.isInQuietHours(LocalTime.of(3, 0))).isTrue();
        }

        @Test
        @DisplayName("跨日靜音（22:00-08:00）- 12:00 不應在靜音時段內")
        void crossDay_12_shouldNotBeInQuietHours() {
            // Given
            QuietHours qh = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));

            // Then
            assertThat(qh.isInQuietHours(LocalTime.of(12, 0))).isFalse();
        }

        @Test
        @DisplayName("同日靜音（08:00-22:00）- 12:00 應在靜音時段內")
        void sameDay_12_shouldBeInQuietHours() {
            // Given
            QuietHours qh = QuietHours.of(LocalTime.of(8, 0), LocalTime.of(22, 0));

            // Then
            assertThat(qh.isInQuietHours(LocalTime.of(12, 0))).isTrue();
        }

        @Test
        @DisplayName("同日靜音（08:00-22:00）- 23:00 不應在靜音時段內")
        void sameDay_23_shouldNotBeInQuietHours() {
            // Given
            QuietHours qh = QuietHours.of(LocalTime.of(8, 0), LocalTime.of(22, 0));

            // Then
            assertThat(qh.isInQuietHours(LocalTime.of(23, 0))).isFalse();
        }

        @Test
        @DisplayName("跨日靜音 - 邊界值：正好在開始時間應在靜音時段內")
        void crossDay_exactStartTime_shouldBeInQuietHours() {
            // Given
            QuietHours qh = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));

            // Then
            assertThat(qh.isInQuietHours(LocalTime.of(22, 0))).isTrue();
        }

        @Test
        @DisplayName("跨日靜音 - 邊界值：正好在結束時間不應在靜音時段內")
        void crossDay_exactEndTime_shouldNotBeInQuietHours() {
            // Given
            QuietHours qh = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));

            // Then
            assertThat(qh.isInQuietHours(LocalTime.of(8, 0))).isFalse();
        }
    }

    @Nested
    @DisplayName("equals 與 hashCode 測試")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("相同值的 QuietHours 應相等")
        void sameValues_shouldBeEqual() {
            // Given
            QuietHours qh1 = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));
            QuietHours qh2 = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));

            // Then
            assertThat(qh1).isEqualTo(qh2);
            assertThat(qh1.hashCode()).isEqualTo(qh2.hashCode());
        }

        @Test
        @DisplayName("不同值的 QuietHours 不應相等")
        void differentValues_shouldNotBeEqual() {
            // Given
            QuietHours qh1 = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));
            QuietHours qh2 = QuietHours.of(LocalTime.of(23, 0), LocalTime.of(7, 0));

            // Then
            assertThat(qh1).isNotEqualTo(qh2);
        }

        @Test
        @DisplayName("停用的 QuietHours 之間應相等")
        void disabled_shouldBeEqual() {
            // Given
            QuietHours qh1 = QuietHours.disabled();
            QuietHours qh2 = QuietHours.disabled();

            // Then
            assertThat(qh1).isEqualTo(qh2);
        }
    }

    @Nested
    @DisplayName("toString 測試")
    class ToStringTests {

        @Test
        @DisplayName("停用時應顯示 disabled")
        void disabled_shouldShowDisabled() {
            assertThat(QuietHours.disabled().toString()).contains("disabled");
        }

        @Test
        @DisplayName("啟用時應顯示時間範圍")
        void enabled_shouldShowTimeRange() {
            QuietHours qh = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));
            assertThat(qh.toString()).contains("22:00").contains("08:00");
        }
    }
}
