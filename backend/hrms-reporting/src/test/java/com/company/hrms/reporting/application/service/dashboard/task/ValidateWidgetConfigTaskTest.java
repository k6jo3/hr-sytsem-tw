package com.company.hrms.reporting.application.service.dashboard.task;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.reporting.api.request.CreateDashboardRequest;
import com.company.hrms.reporting.api.request.CreateDashboardRequest.WidgetConfigDto;
import com.company.hrms.reporting.application.service.dashboard.context.CreateDashboardContext;

/**
 * ValidateWidgetConfigTask 單元測試
 * <p>
 * 測試 Widget 配置驗證與轉換邏輯
 * </p>
 *
 * @author Claude
 * @since 2026-03-19
 */
@DisplayName("ValidateWidgetConfigTask 單元測試")
class ValidateWidgetConfigTaskTest {

    private ValidateWidgetConfigTask task;

    @BeforeEach
    void setUp() {
        task = new ValidateWidgetConfigTask();
    }

    @Nested
    @DisplayName("execute - Widget 配置驗證與轉換")
    class ExecuteTests {

        @Test
        @DisplayName("Widget 列表為 null 時，應設定為空列表")
        void shouldSetEmptyListWhenWidgetsIsNull() throws Exception {
            // Given
            CreateDashboardRequest request = new CreateDashboardRequest();
            request.setDashboardName("測試");
            request.setWidgets(null);
            CreateDashboardContext context = new CreateDashboardContext(request, "tenant-001", "user-001");

            // When
            task.execute(context);

            // Then
            assertThat(context.getWidgets()).isEmpty();
        }

        @Test
        @DisplayName("Widget 列表為空時，應設定為空列表")
        void shouldSetEmptyListWhenWidgetsIsEmpty() throws Exception {
            // Given
            CreateDashboardRequest request = new CreateDashboardRequest();
            request.setDashboardName("測試");
            request.setWidgets(List.of());
            CreateDashboardContext context = new CreateDashboardContext(request, "tenant-001", "user-001");

            // When
            task.execute(context);

            // Then
            assertThat(context.getWidgets()).isEmpty();
        }

        @Test
        @DisplayName("應正確轉換合法的 Widget DTO")
        void shouldConvertValidWidgetDto() throws Exception {
            // Given
            WidgetConfigDto dto = new WidgetConfigDto();
            dto.setWidgetId("w1");
            dto.setWidgetType("KPI_CARD");
            dto.setTitle("本月營收");
            dto.setDataSource("monthly_revenue");
            dto.setX(0);
            dto.setY(0);
            dto.setW(3);
            dto.setH(2);

            CreateDashboardRequest request = new CreateDashboardRequest();
            request.setDashboardName("測試");
            List<WidgetConfigDto> widgets = new ArrayList<>();
            widgets.add(dto);
            request.setWidgets(widgets);
            CreateDashboardContext context = new CreateDashboardContext(request, "tenant-001", "user-001");

            // When
            task.execute(context);

            // Then
            assertThat(context.getWidgets()).hasSize(1);
            assertThat(context.getWidgets().get(0).getWidgetId()).isEqualTo("w1");
            assertThat(context.getWidgets().get(0).getTitle()).isEqualTo("本月營收");
        }

        @Test
        @DisplayName("DTO 座標為 null 時，應使用預設值")
        void shouldUseDefaultsWhenPositionFieldsAreNull() throws Exception {
            // Given
            WidgetConfigDto dto = new WidgetConfigDto();
            dto.setWidgetId("w1");
            dto.setWidgetType("KPI_CARD");
            dto.setTitle("測試");
            dto.setDataSource("test_data");
            // x, y, w, h 都不設定（null）

            CreateDashboardRequest request = new CreateDashboardRequest();
            request.setDashboardName("測試");
            List<WidgetConfigDto> widgets = new ArrayList<>();
            widgets.add(dto);
            request.setWidgets(widgets);
            CreateDashboardContext context = new CreateDashboardContext(request, "tenant-001", "user-001");

            // When
            task.execute(context);

            // Then
            assertThat(context.getWidgets()).hasSize(1);
            assertThat(context.getWidgets().get(0).getPosition().getX()).isEqualTo(0);
            assertThat(context.getWidgets().get(0).getPosition().getY()).isEqualTo(0);
            assertThat(context.getWidgets().get(0).getPosition().getW()).isEqualTo(3);
            assertThat(context.getWidgets().get(0).getPosition().getH()).isEqualTo(2);
        }

        @Test
        @DisplayName("WidgetType 為 null 時，應使用預設 KPI_CARD")
        void shouldUseDefaultWidgetTypeWhenNull() throws Exception {
            // Given
            WidgetConfigDto dto = new WidgetConfigDto();
            dto.setWidgetId("w1");
            dto.setWidgetType(null);
            dto.setTitle("測試");
            dto.setDataSource("test_data");
            dto.setX(0);
            dto.setY(0);
            dto.setW(3);
            dto.setH(2);

            CreateDashboardRequest request = new CreateDashboardRequest();
            request.setDashboardName("測試");
            List<WidgetConfigDto> widgets = new ArrayList<>();
            widgets.add(dto);
            request.setWidgets(widgets);
            CreateDashboardContext context = new CreateDashboardContext(request, "tenant-001", "user-001");

            // When
            task.execute(context);

            // Then
            assertThat(context.getWidgets().get(0).getWidgetType())
                    .isEqualTo(com.company.hrms.reporting.domain.model.dashboard.DashboardWidget.WidgetType.KPI_CARD);
        }

        @Test
        @DisplayName("Widget 驗證失敗時（如 title 為空），應拋出例外")
        void shouldThrowWhenWidgetValidationFails() {
            // Given
            WidgetConfigDto dto = new WidgetConfigDto();
            dto.setWidgetId("w1");
            dto.setWidgetType("KPI_CARD");
            dto.setTitle(""); // 空標題
            dto.setDataSource("data");
            dto.setX(0);
            dto.setY(0);
            dto.setW(3);
            dto.setH(2);

            CreateDashboardRequest request = new CreateDashboardRequest();
            request.setDashboardName("測試");
            List<WidgetConfigDto> widgets = new ArrayList<>();
            widgets.add(dto);
            request.setWidgets(widgets);
            CreateDashboardContext context = new CreateDashboardContext(request, "tenant-001", "user-001");

            // When & Then
            assertThatThrownBy(() -> task.execute(context))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Widget 標題不可為空");
        }
    }
}
