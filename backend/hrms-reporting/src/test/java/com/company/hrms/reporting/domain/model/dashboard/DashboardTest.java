package com.company.hrms.reporting.domain.model.dashboard;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Dashboard 聚合根測試
 * 
 * <p>
 * TDD 測試：先寫測試，再實作
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@DisplayName("Dashboard 聚合根測試")
class DashboardTest {

    @Test
    @DisplayName("建立儀表板 - 應成功建立並設定基本屬性")
    void createDashboard_ShouldSetBasicProperties() {
        // Given
        String dashboardName = "高階主管儀表板";
        String description = "CEO 每日經營數據";
        UUID ownerId = UUID.randomUUID();
        String tenantId = "tenant-001";

        // When
        Dashboard dashboard = Dashboard.create(
                dashboardName, description, ownerId, tenantId, false);

        // Then
        assertThat(dashboard.getId()).isNotNull();
        assertThat(dashboard.getDashboardName()).isEqualTo(dashboardName);
        assertThat(dashboard.getDescription()).isEqualTo(description);
        assertThat(dashboard.getOwnerId()).isEqualTo(ownerId);
        assertThat(dashboard.getTenantId()).isEqualTo(tenantId);
        assertThat(dashboard.isPublic()).isFalse();
        assertThat(dashboard.isDefault()).isFalse();
        assertThat(dashboard.getWidgets()).isEmpty();
        assertThat(dashboard.getCreatedAt()).isNotNull();
        assertThat(dashboard.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("建立儀表板 - 名稱為空應拋出例外")
    void createDashboard_WithEmptyName_ShouldThrowException() {
        // Given
        String emptyName = "";

        // When & Then
        assertThatThrownBy(() -> Dashboard.create(emptyName, "desc", UUID.randomUUID(), "tenant-001", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("儀表板名稱不可為空");
    }

    @Test
    @DisplayName("建立儀表板 - 名稱超過100字元應拋出例外")
    void createDashboard_WithTooLongName_ShouldThrowException() {
        // Given
        String longName = "A".repeat(101);

        // When & Then
        assertThatThrownBy(() -> Dashboard.create(longName, "desc", UUID.randomUUID(), "tenant-001", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不可超過 100 字元");
    }

    @Test
    @DisplayName("新增Widget - 應成功新增並更新時間")
    void addWidget_ShouldAddWidgetAndUpdateTime() {
        // Given
        Dashboard dashboard = createTestDashboard();
        DashboardWidget widget = createTestWidget("w1", 0, 0, 3, 2);

        // When
        dashboard.addWidget(widget);

        // Then
        assertThat(dashboard.getWidgets()).hasSize(1);
        assertThat(dashboard.getWidgets().get(0).getWidgetId()).isEqualTo("w1");
    }

    @Test
    @DisplayName("新增Widget - 位置重疊應拋出例外")
    void addWidget_WithOverlappingPosition_ShouldThrowException() {
        // Given
        Dashboard dashboard = createTestDashboard();
        DashboardWidget widget1 = createTestWidget("w1", 0, 0, 3, 2);
        DashboardWidget widget2 = createTestWidget("w2", 1, 1, 3, 2); // 重疊

        dashboard.addWidget(widget1);

        // When & Then
        assertThatThrownBy(() -> dashboard.addWidget(widget2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Widget 位置重疊");
    }

    @Test
    @DisplayName("新增Widget - 不重疊位置應成功")
    void addWidget_WithNonOverlappingPosition_ShouldSucceed() {
        // Given
        Dashboard dashboard = createTestDashboard();
        DashboardWidget widget1 = createTestWidget("w1", 0, 0, 3, 2);
        DashboardWidget widget2 = createTestWidget("w2", 3, 0, 3, 2); // 不重疊

        dashboard.addWidget(widget1);

        // When
        dashboard.addWidget(widget2);

        // Then
        assertThat(dashboard.getWidgets()).hasSize(2);
    }

    @Test
    @DisplayName("移除Widget - 應成功移除")
    void removeWidget_ShouldRemoveWidget() {
        // Given
        Dashboard dashboard = createTestDashboard();
        DashboardWidget widget = createTestWidget("w1", 0, 0, 3, 2);
        dashboard.addWidget(widget);

        // When
        dashboard.removeWidget("w1");

        // Then
        assertThat(dashboard.getWidgets()).isEmpty();
    }

    @Test
    @DisplayName("更新Widgets - 應替換所有Widget")
    void updateWidgets_ShouldReplaceAllWidgets() {
        // Given
        Dashboard dashboard = createTestDashboard();
        dashboard.addWidget(createTestWidget("w1", 0, 0, 3, 2));

        DashboardWidget newWidget1 = createTestWidget("w2", 0, 0, 4, 2);
        DashboardWidget newWidget2 = createTestWidget("w3", 4, 0, 4, 2);

        // When
        dashboard.updateWidgets(java.util.Arrays.asList(newWidget1, newWidget2));

        // Then
        assertThat(dashboard.getWidgets()).hasSize(2);
        assertThat(dashboard.getWidgets().get(0).getWidgetId()).isEqualTo("w2");
        assertThat(dashboard.getWidgets().get(1).getWidgetId()).isEqualTo("w3");
    }

    @Test
    @DisplayName("設定為預設儀表板 - 應更新isDefault旗標")
    void setAsDefault_ShouldUpdateFlag() {
        // Given
        Dashboard dashboard = createTestDashboard();

        // When
        dashboard.setAsDefault();

        // Then
        assertThat(dashboard.isDefault()).isTrue();
    }

    // === 測試輔助方法 ===

    private Dashboard createTestDashboard() {
        return Dashboard.create(
                "測試儀表板",
                "測試用",
                UUID.randomUUID(),
                "tenant-001",
                false);
    }

    private DashboardWidget createTestWidget(String id, int x, int y, int w, int h) {
        return DashboardWidget.create(
                id,
                DashboardWidget.WidgetType.KPI_CARD,
                "測試Widget",
                "test_data_source",
                new WidgetPosition(x, y, w, h));
    }
}
