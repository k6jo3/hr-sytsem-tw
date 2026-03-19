package com.company.hrms.notification.domain.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.model.valueobject.NotificationType;

/**
 * TemplateRendererService 領域服務單元測試
 * <p>
 * 測試範本渲染邏輯：變數替換、主旨渲染、變數完整性驗證
 * </p>
 *
 * @author Claude
 * @since 2026-03-19
 */
@DisplayName("TemplateRendererService 單元測試")
class TemplateRendererServiceTest {

    private TemplateRendererService service;

    @BeforeEach
    void setUp() {
        service = new TemplateRendererService();
    }

    @Nested
    @DisplayName("renderContent - 渲染範本內容")
    class RenderContentTests {

        @Test
        @DisplayName("範本為 null 時應拋出例外")
        void shouldThrowWhenTemplateIsNull() {
            assertThatThrownBy(() -> service.renderContent(null, Map.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("範本不可為空");
        }

        @Test
        @DisplayName("應正確替換範本變數")
        void shouldReplaceVariables() {
            // Given
            NotificationTemplate template = createTemplate(
                    "請假通知",
                    "{{employeeName}} 申請了 {{leaveDays}} 天假期");
            Map<String, Object> variables = Map.of(
                    "employeeName", "王大明",
                    "leaveDays", 3);

            // When
            String result = service.renderContent(template, variables);

            // Then
            assertThat(result).isEqualTo("王大明 申請了 3 天假期");
        }

        @Test
        @DisplayName("變數不存在時應替換為空字串")
        void shouldReplaceWithEmptyWhenVariableMissing() {
            // Given
            NotificationTemplate template = createTemplate(
                    "通知",
                    "你好 {{name}}，{{message}}");
            Map<String, Object> variables = Map.of("name", "張三");

            // When
            String result = service.renderContent(template, variables);

            // Then
            assertThat(result).isEqualTo("你好 張三，");
        }
    }

    @Nested
    @DisplayName("renderSubject - 渲染範本主旨")
    class RenderSubjectTests {

        @Test
        @DisplayName("範本為 null 時應拋出例外")
        void shouldThrowWhenTemplateIsNull() {
            assertThatThrownBy(() -> service.renderSubject(null, Map.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("範本不可為空");
        }

        @Test
        @DisplayName("應正確替換主旨變數")
        void shouldReplaceSubjectVariables() {
            // Given
            NotificationTemplate template = createTemplateWithSubject(
                    "【{{type}}】{{title}}",
                    "內容");
            Map<String, Object> variables = Map.of("type", "請假", "title", "待審核");

            // When
            String result = service.renderSubject(template, variables);

            // Then
            assertThat(result).isEqualTo("【請假】待審核");
        }
    }

    @Nested
    @DisplayName("validateVariables - 驗證變數完整性")
    class ValidateVariablesTests {

        @Test
        @DisplayName("範本為 null 時應回傳 false")
        void shouldReturnFalseWhenTemplateIsNull() {
            assertThat(service.validateVariables(null, Map.of())).isFalse();
        }

        @Test
        @DisplayName("所有變數都有提供時應回傳 true")
        void shouldReturnTrueWhenAllVariablesProvided() {
            // Given
            NotificationTemplate template = createTemplate(
                    "通知",
                    "{{name}} 的 {{action}} 已完成");
            Map<String, Object> variables = Map.of("name", "張三", "action", "請假");

            // Then
            assertThat(service.validateVariables(template, variables)).isTrue();
        }

        @Test
        @DisplayName("缺少變數時應回傳 false")
        void shouldReturnFalseWhenVariableMissing() {
            // Given
            NotificationTemplate template = createTemplate(
                    "通知",
                    "{{name}} 的 {{action}} 已完成");
            Map<String, Object> variables = Map.of("name", "張三");

            // Then
            assertThat(service.validateVariables(template, variables)).isFalse();
        }

        @Test
        @DisplayName("variables 為 null 且範本有變數時應回傳 false")
        void shouldReturnFalseWhenVariablesIsNullAndTemplateHasVars() {
            // Given
            NotificationTemplate template = createTemplate(
                    "通知",
                    "你好 {{name}}");

            // Then
            assertThat(service.validateVariables(template, null)).isFalse();
        }

        @Test
        @DisplayName("範本無變數時應回傳 true")
        void shouldReturnTrueWhenTemplateHasNoVariables() {
            // Given
            NotificationTemplate template = createTemplate(
                    "通知",
                    "這是一則沒有變數的通知內容");

            // Then
            assertThat(service.validateVariables(template, Map.of())).isTrue();
        }
    }

    // ==================== 輔助方法 ====================

    private NotificationTemplate createTemplate(String name, String body) {
        return NotificationTemplate.create(
                "TPL_TEST",
                name,
                null, // subject
                body,
                NotificationType.ANNOUNCEMENT,
                NotificationPriority.NORMAL,
                List.of(NotificationChannel.IN_APP));
    }

    private NotificationTemplate createTemplateWithSubject(String subject, String body) {
        return NotificationTemplate.create(
                "TPL_TEST",
                "測試範本",
                subject,
                body,
                NotificationType.ANNOUNCEMENT,
                NotificationPriority.NORMAL,
                List.of(NotificationChannel.IN_APP));
    }
}
