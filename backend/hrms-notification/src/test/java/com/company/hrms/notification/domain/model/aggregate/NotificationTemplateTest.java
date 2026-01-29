package com.company.hrms.notification.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.model.valueobject.NotificationType;

/**
 * NotificationTemplate 聚合根單元測試
 *
 * @author Claude
 * @since 2026-01-29
 */
@DisplayName("NotificationTemplate 聚合根測試")
class NotificationTemplateTest {

    @Nested
    @DisplayName("建立範本測試")
    class CreateTemplateTests {

        @Test
        @DisplayName("應該成功建立通知範本")
        void shouldCreateTemplateSuccessfully() {
            // Given
            String templateCode = "LEAVE_APPROVED";
            String templateName = "請假核准通知";
            String subject = "您的請假申請已核准";
            String body = "親愛的 {{employeeName}}，您的請假申請（{{leaveType}}）已經核准。";
            NotificationType type = NotificationType.APPROVAL_RESULT;
            NotificationPriority priority = NotificationPriority.NORMAL;
            List<NotificationChannel> channels = List.of(NotificationChannel.IN_APP, NotificationChannel.EMAIL);

            // When
            NotificationTemplate template = NotificationTemplate.create(
                    templateCode, templateName, subject, body, type, priority, channels);

            // Then
            assertThat(template).isNotNull();
            assertThat(template.getId()).isNotNull();
            assertThat(template.getTemplateCode()).isEqualTo(templateCode);
            assertThat(template.getTemplateName()).isEqualTo(templateName);
            assertThat(template.getSubject()).isEqualTo(subject);
            assertThat(template.getBody()).isEqualTo(body);
            assertThat(template.getNotificationType()).isEqualTo(type);
            assertThat(template.getDefaultPriority()).isEqualTo(priority);
            assertThat(template.getDefaultChannels()).containsExactlyElementsOf(channels);
            assertThat(template.isActive()).isTrue(); // 預設啟用
        }

        @Test
        @DisplayName("應該拋出例外當範本代碼為 null")
        void shouldThrowExceptionWhenTemplateCodeIsNull() {
            // When & Then
            assertThatThrownBy(() -> NotificationTemplate.create(
                    null, "範本名稱", "主旨", "內容", NotificationType.ANNOUNCEMENT,
                    NotificationPriority.NORMAL, List.of(NotificationChannel.IN_APP)))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("範本代碼");
        }

        @Test
        @DisplayName("應該拋出例外當範本名稱為空")
        void shouldThrowExceptionWhenTemplateNameIsBlank() {
            // When & Then
            assertThatThrownBy(() -> NotificationTemplate.create(
                    "CODE", "", "主旨", "內容", NotificationType.ANNOUNCEMENT,
                    NotificationPriority.NORMAL, List.of(NotificationChannel.IN_APP)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("範本名稱");
        }
    }

    @Nested
    @DisplayName("範本內容更新測試")
    class UpdateContentTests {

        @Test
        @DisplayName("應該成功更新範本內容")
        void shouldUpdateContentSuccessfully() {
            // Given
            NotificationTemplate template = createTestTemplate();
            String newName = "新範本名稱";
            String newSubject = "新主旨";
            String newBody = "新內容 {{variable}}";

            // When
            template.updateContent(newName, newSubject, newBody);

            // Then
            assertThat(template.getTemplateName()).isEqualTo(newName);
            assertThat(template.getSubject()).isEqualTo(newSubject);
            assertThat(template.getBody()).isEqualTo(newBody);
        }

        @Test
        @DisplayName("應該成功更新預設渠道")
        void shouldUpdateDefaultChannelsSuccessfully() {
            // Given
            NotificationTemplate template = createTestTemplate();
            List<NotificationChannel> newChannels = List.of(
                    NotificationChannel.IN_APP,
                    NotificationChannel.EMAIL,
                    NotificationChannel.PUSH);

            // When
            template.updateDefaultChannels(newChannels);

            // Then
            assertThat(template.getDefaultChannels()).containsExactlyElementsOf(newChannels);
        }
    }

    @Nested
    @DisplayName("範本啟用/停用測試")
    class ActivationTests {

        @Test
        @DisplayName("應該成功停用範本")
        void shouldDeactivateTemplateSuccessfully() {
            // Given
            NotificationTemplate template = createTestTemplate();
            assertThat(template.isActive()).isTrue();

            // When
            template.deactivate();

            // Then
            assertThat(template.isActive()).isFalse();
        }

        @Test
        @DisplayName("應該成功啟用範本")
        void shouldActivateTemplateSuccessfully() {
            // Given
            NotificationTemplate template = createTestTemplate();
            template.deactivate();
            assertThat(template.isActive()).isFalse();

            // When
            template.activate();

            // Then
            assertThat(template.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("變數替換測試")
    class VariableReplacementTests {

        @Test
        @DisplayName("應該成功替換內容中的變數")
        void shouldRenderContentWithVariables() {
            // Given
            NotificationTemplate template = NotificationTemplate.create(
                    "TEST_TEMPLATE",
                    "測試範本",
                    "測試主旨",
                    "您好 {{employeeName}}，您的 {{leaveType}} 申請已提交。",
                    NotificationType.ANNOUNCEMENT,
                    NotificationPriority.NORMAL,
                    List.of(NotificationChannel.IN_APP));

            Map<String, Object> variables = Map.of(
                    "employeeName", "王小明",
                    "leaveType", "特休假");

            // When
            String renderedContent = template.renderContent(variables);

            // Then
            assertThat(renderedContent).isEqualTo("您好 王小明，您的 特休假 申請已提交。");
        }

        @Test
        @DisplayName("應該成功替換主旨中的變數")
        void shouldRenderSubjectWithVariables() {
            // Given
            NotificationTemplate template = NotificationTemplate.create(
                    "TEST_TEMPLATE",
                    "測試範本",
                    "{{employeeName}} 的請假申請",
                    "內容",
                    NotificationType.ANNOUNCEMENT,
                    NotificationPriority.NORMAL,
                    List.of(NotificationChannel.IN_APP));

            Map<String, Object> variables = Map.of("employeeName", "王小明");

            // When
            String renderedSubject = template.renderSubject(variables);

            // Then
            assertThat(renderedSubject).isEqualTo("王小明 的請假申請");
        }

        @Test
        @DisplayName("當變數不存在時應該保留原始佔位符")
        void shouldKeepPlaceholderWhenVariableNotProvided() {
            // Given
            NotificationTemplate template = NotificationTemplate.create(
                    "TEST_TEMPLATE",
                    "測試範本",
                    "測試主旨",
                    "您好 {{employeeName}}，您的申請已提交。",
                    NotificationType.ANNOUNCEMENT,
                    NotificationPriority.NORMAL,
                    List.of(NotificationChannel.IN_APP));

            Map<String, Object> variables = Map.of(); // 空變數

            // When
            String renderedContent = template.renderContent(variables);

            // Then
            assertThat(renderedContent).contains("{{employeeName}}"); // 保留佔位符
        }
    }

    // ==================== Helper Methods ====================

    private NotificationTemplate createTestTemplate() {
        return NotificationTemplate.create(
                "TEST_TEMPLATE",
                "測試範本",
                "測試主旨",
                "測試內容 {{variable}}",
                NotificationType.ANNOUNCEMENT,
                NotificationPriority.NORMAL,
                List.of(NotificationChannel.IN_APP));
    }
}
