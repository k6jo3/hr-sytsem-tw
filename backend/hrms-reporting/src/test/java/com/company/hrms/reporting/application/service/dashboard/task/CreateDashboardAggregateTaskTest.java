package com.company.hrms.reporting.application.service.dashboard.task;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.reporting.api.request.CreateDashboardRequest;
import com.company.hrms.reporting.application.service.dashboard.context.CreateDashboardContext;
import com.company.hrms.reporting.domain.model.dashboard.DashboardWidget;
import com.company.hrms.reporting.domain.model.dashboard.WidgetPosition;

/**
 * CreateDashboardAggregateTask 單元測試
 * <p>
 * 測試 Dashboard 聚合根建立邏輯
 * </p>
 *
 * @author Claude
 * @since 2026-03-19
 */
@DisplayName("CreateDashboardAggregateTask 單元測試")
class CreateDashboardAggregateTaskTest {

    private CreateDashboardAggregateTask task;

    @BeforeEach
    void setUp() {
        task = new CreateDashboardAggregateTask();
    }

    @Nested
    @DisplayName("execute - 建立 Dashboard 聚合根")
    class ExecuteTests {

        @Test
        @DisplayName("應成功建立 Dashboard 並設定到 context")
        void shouldCreateDashboardAndSetToContext() throws Exception {
            // Given
            String userId = UUID.randomUUID().toString();
            CreateDashboardRequest request = new CreateDashboardRequest();
            request.setDashboardName("高階主管儀表板");
            request.setDescription("CEO 每日數據");
            request.setIsPublic(false);

            CreateDashboardContext context = new CreateDashboardContext(request, "tenant-001", userId);
            context.setWidgets(List.of());

            // When
            task.execute(context);

            // Then
            assertThat(context.getDashboard()).isNotNull();
            assertThat(context.getDashboard().getDashboardName()).isEqualTo("高階主管儀表板");
            assertThat(context.getDashboard().getDescription()).isEqualTo("CEO 每日數據");
            assertThat(context.getDashboard().getTenantId()).isEqualTo("tenant-001");
            assertThat(context.getDashboard().isPublic()).isFalse();
            assertThat(context.getDashboard().getWidgets()).isEmpty();
        }

        @Test
        @DisplayName("應正確新增 Widgets 到 Dashboard")
        void shouldAddWidgetsToDashboard() throws Exception {
            // Given
            String userId = UUID.randomUUID().toString();
            CreateDashboardRequest request = new CreateDashboardRequest();
            request.setDashboardName("測試儀表板");
            request.setDescription("描述");
            request.setIsPublic(true);

            CreateDashboardContext context = new CreateDashboardContext(request, "tenant-001", userId);

            DashboardWidget widget = DashboardWidget.create(
                    "w1", DashboardWidget.WidgetType.KPI_CARD, "營收",
                    "revenue", new WidgetPosition(0, 0, 3, 2));
            context.setWidgets(List.of(widget));

            // When
            task.execute(context);

            // Then
            assertThat(context.getDashboard().getWidgets()).hasSize(1);
            assertThat(context.getDashboard().getWidgets().get(0).getWidgetId()).isEqualTo("w1");
            assertThat(context.getDashboard().isPublic()).isTrue();
        }

        @Test
        @DisplayName("isPublic 為 null 時，應預設為 false")
        void shouldDefaultIsPublicToFalseWhenNull() throws Exception {
            // Given
            String userId = UUID.randomUUID().toString();
            CreateDashboardRequest request = new CreateDashboardRequest();
            request.setDashboardName("測試");
            request.setIsPublic(null);

            CreateDashboardContext context = new CreateDashboardContext(request, "tenant-001", userId);
            context.setWidgets(List.of());

            // When
            task.execute(context);

            // Then
            assertThat(context.getDashboard().isPublic()).isFalse();
        }

        @Test
        @DisplayName("Widget 為 null 時不應拋出例外")
        void shouldNotThrowWhenWidgetsIsNull() throws Exception {
            // Given
            String userId = UUID.randomUUID().toString();
            CreateDashboardRequest request = new CreateDashboardRequest();
            request.setDashboardName("測試");

            CreateDashboardContext context = new CreateDashboardContext(request, "tenant-001", userId);
            context.setWidgets(null);

            // When
            task.execute(context);

            // Then
            assertThat(context.getDashboard()).isNotNull();
            assertThat(context.getDashboard().getWidgets()).isEmpty();
        }
    }
}
