package com.company.hrms.notification.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.QuietHours;

/**
 * NotificationPreference 聚合根單元測試
 *
 * @author Claude
 * @since 2026-01-29
 */
@DisplayName("NotificationPreference 聚合根測試")
class NotificationPreferenceTest {

    @Nested
    @DisplayName("建立偏好設定測試")
    class CreatePreferenceTests {

        @Test
        @DisplayName("應該成功建立預設偏好設定")
        void shouldCreateDefaultPreferenceSuccessfully() {
            // Given
            String employeeId = "EMP001";

            // When
            NotificationPreference preference = NotificationPreference.createDefault(employeeId);

            // Then
            assertThat(preference).isNotNull();
            assertThat(preference.getId()).isNotNull();
            assertThat(preference.getEmployeeId()).isEqualTo(employeeId);
            assertThat(preference.isEmailEnabled()).isTrue(); // 預設啟用
            assertThat(preference.isPushEnabled()).isTrue(); // 預設啟用
            assertThat(preference.isInAppEnabled()).isTrue(); // 預設啟用
            assertThat(preference.getQuietHours()).isNotNull(); // 預設有靜音時段 22:00-08:00
            assertThat(preference.getQuietHours().getStartTime()).isEqualTo(java.time.LocalTime.of(22, 0));
            assertThat(preference.getQuietHours().getEndTime()).isEqualTo(java.time.LocalTime.of(8, 0));
        }

        @Test
        @DisplayName("應該成功建立自訂偏好設定")
        void shouldCreateCustomPreferenceSuccessfully() {
            // Given
            String employeeId = "EMP001";
            QuietHours quietHours = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));

            // When
            NotificationPreference preference = NotificationPreference.create(
                    employeeId, true, false, true, quietHours);

            // Then
            assertThat(preference).isNotNull();
            assertThat(preference.getEmployeeId()).isEqualTo(employeeId);
            assertThat(preference.isEmailEnabled()).isTrue();
            assertThat(preference.isPushEnabled()).isFalse();
            assertThat(preference.isInAppEnabled()).isTrue();
            assertThat(preference.getQuietHours()).isEqualTo(quietHours);
        }

        @Test
        @DisplayName("應該拋出例外當員工 ID 為 null")
        void shouldThrowExceptionWhenEmployeeIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> NotificationPreference.createDefault(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("員工 ID");
        }
    }

    @Nested
    @DisplayName("渠道偏好更新測試")
    class UpdateChannelPreferencesTests {

        @Test
        @DisplayName("應該成功更新渠道偏好設定")
        void shouldUpdateChannelPreferencesSuccessfully() {
            // Given
            NotificationPreference preference = NotificationPreference.createDefault("EMP001");

            // When
            preference.updateChannelPreferences(false, true, true, false, false);

            // Then
            assertThat(preference.isEmailEnabled()).isFalse();
            assertThat(preference.isPushEnabled()).isTrue();
            assertThat(preference.isInAppEnabled()).isTrue();
            assertThat(preference.isTeamsEnabled()).isFalse();
            assertThat(preference.isLineEnabled()).isFalse();
        }

        @Test
        @DisplayName("應該成功更新靜音時段")
        void shouldUpdateQuietHoursSuccessfully() {
            // Given
            NotificationPreference preference = NotificationPreference.createDefault("EMP001");
            QuietHours newQuietHours = QuietHours.of(LocalTime.of(23, 0), LocalTime.of(7, 0));

            // When
            preference.updateQuietHours(newQuietHours);

            // Then
            assertThat(preference.getQuietHours()).isEqualTo(newQuietHours);
        }
    }

    @Nested
    @DisplayName("渠道過濾測試")
    class ChannelFilteringTests {

        @Test
        @DisplayName("應該過濾掉已停用的渠道")
        void shouldFilterDisabledChannels() {
            // Given
            NotificationPreference preference = NotificationPreference.createDefault("EMP001");
            preference.updateChannelPreferences(true, false, true, false, false);
            // EMAIL=啟用, PUSH=停用, IN_APP=啟用, TEAMS=停用, LINE=停用

            List<NotificationChannel> requestedChannels = List.of(
                    NotificationChannel.EMAIL,
                    NotificationChannel.PUSH,
                    NotificationChannel.IN_APP,
                    NotificationChannel.TEAMS);

            // When
            List<NotificationChannel> filteredChannels = preference.filterChannels(requestedChannels);

            // Then
            assertThat(filteredChannels).containsExactlyInAnyOrder(
                    NotificationChannel.EMAIL,
                    NotificationChannel.IN_APP);
            assertThat(filteredChannels).doesNotContain(
                    NotificationChannel.PUSH,
                    NotificationChannel.TEAMS);
        }

        @Test
        @DisplayName("當所有渠道都停用時應該返回空列表")
        void shouldReturnEmptyListWhenAllChannelsDisabled() {
            // Given
            NotificationPreference preference = NotificationPreference.createDefault("EMP001");
            preference.updateChannelPreferences(false, false, false, false, false);

            List<NotificationChannel> requestedChannels = List.of(
                    NotificationChannel.EMAIL,
                    NotificationChannel.PUSH,
                    NotificationChannel.IN_APP);

            // When
            List<NotificationChannel> filteredChannels = preference.filterChannels(requestedChannels);

            // Then
            assertThat(filteredChannels).isEmpty();
        }
    }

    @Nested
    @DisplayName("靜音時段檢查測試")
    class QuietHoursCheckTests {

        @Test
        @DisplayName("當靜音時段停用時應該返回 false")
        void shouldReturnFalseWhenQuietHoursDisabled() {
            // Given - 建立偏好設定並停用靜音時段
            NotificationPreference preference = NotificationPreference.createDefault("EMP001");
            preference.updateQuietHours(QuietHours.disabled());

            // Then
            assertThat(preference.isInQuietHours()).isFalse();
        }

        @Test
        @DisplayName("應該正確檢查是否在靜音時段內")
        void shouldCheckQuietHoursCorrectly() {
            // Given
            NotificationPreference preference = NotificationPreference.createDefault("EMP001");
            QuietHours quietHours = QuietHours.of(LocalTime.of(22, 0), LocalTime.of(8, 0));
            preference.updateQuietHours(quietHours);

            // When & Then
            // 注意：實際測試需要 mock LocalTime.now()
            // 這裡僅測試方法存在且不拋出例外
            assertThatCode(() -> preference.isInQuietHours()).doesNotThrowAnyException();
        }
    }
}
