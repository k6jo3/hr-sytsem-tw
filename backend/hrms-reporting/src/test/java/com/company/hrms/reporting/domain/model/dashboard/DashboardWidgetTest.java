package com.company.hrms.reporting.domain.model.dashboard;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * DashboardWidget 值物件單元測試
 * <p>
 * 測試 Widget 建立、驗證、相等性邏輯
 * </p>
 *
 * @author Claude
 * @since 2026-03-19
 */
@DisplayName("DashboardWidget 值物件測試")
class DashboardWidgetTest {

    @Nested
    @DisplayName("create - 建立 Widget")
    class CreateTests {

        @Test
        @DisplayName("應成功建立 Widget 並設定預設刷新間隔")
        void shouldCreateWithDefaultRefreshInterval() {
            // When
            DashboardWidget widget = DashboardWidget.create(
                    "w1",
                    DashboardWidget.WidgetType.KPI_CARD,
                    "本月營收",
                    "monthly_revenue",
                    new WidgetPosition(0, 0, 3, 2));

            // Then
            assertThat(widget.getWidgetId()).isEqualTo("w1");
            assertThat(widget.getWidgetType()).isEqualTo(DashboardWidget.WidgetType.KPI_CARD);
            assertThat(widget.getTitle()).isEqualTo("本月營收");
            assertThat(widget.getDataSource()).isEqualTo("monthly_revenue");
            assertThat(widget.getRefreshInterval()).isEqualTo(3600);
            assertThat(widget.getStyleConfig()).isNull();
            assertThat(widget.getChartConfig()).isNull();
        }
    }

    @Nested
    @DisplayName("validate - Widget 驗證")
    class ValidateTests {

        @Test
        @DisplayName("Widget ID 為空應拋出例外")
        void shouldThrowWhenWidgetIdIsEmpty() {
            // Given
            DashboardWidget widget = new DashboardWidget(
                    "", DashboardWidget.WidgetType.KPI_CARD, "標題", "data", new WidgetPosition(0, 0, 3, 2), 3600, null, null);

            // Then
            assertThatThrownBy(widget::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Widget ID 不可為空");
        }

        @Test
        @DisplayName("Widget ID 為 null 應拋出例外")
        void shouldThrowWhenWidgetIdIsNull() {
            // Given
            DashboardWidget widget = new DashboardWidget(
                    null, DashboardWidget.WidgetType.KPI_CARD, "標題", "data", new WidgetPosition(0, 0, 3, 2), 3600, null, null);

            // Then
            assertThatThrownBy(widget::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Widget ID 不可為空");
        }

        @Test
        @DisplayName("Widget 類型為 null 應拋出例外")
        void shouldThrowWhenWidgetTypeIsNull() {
            // Given
            DashboardWidget widget = new DashboardWidget(
                    "w1", null, "標題", "data", new WidgetPosition(0, 0, 3, 2), 3600, null, null);

            // Then
            assertThatThrownBy(widget::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Widget 類型不可為空");
        }

        @Test
        @DisplayName("Widget 標題為空應拋出例外")
        void shouldThrowWhenTitleIsEmpty() {
            // Given
            DashboardWidget widget = new DashboardWidget(
                    "w1", DashboardWidget.WidgetType.KPI_CARD, "", "data", new WidgetPosition(0, 0, 3, 2), 3600, null, null);

            // Then
            assertThatThrownBy(widget::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Widget 標題不可為空");
        }

        @Test
        @DisplayName("資料來源為空應拋出例外")
        void shouldThrowWhenDataSourceIsEmpty() {
            // Given
            DashboardWidget widget = new DashboardWidget(
                    "w1", DashboardWidget.WidgetType.KPI_CARD, "標題", "", new WidgetPosition(0, 0, 3, 2), 3600, null, null);

            // Then
            assertThatThrownBy(widget::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("資料來源不可為空");
        }

        @Test
        @DisplayName("位置為 null 應拋出例外")
        void shouldThrowWhenPositionIsNull() {
            // Given
            DashboardWidget widget = new DashboardWidget(
                    "w1", DashboardWidget.WidgetType.KPI_CARD, "標題", "data", null, 3600, null, null);

            // Then
            assertThatThrownBy(widget::validate)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Widget 位置不可為空");
        }

        @Test
        @DisplayName("合法 Widget 應通過驗證")
        void validWidget_shouldPass() {
            // Given
            DashboardWidget widget = DashboardWidget.create(
                    "w1", DashboardWidget.WidgetType.LINE_CHART, "月營收趨勢", "revenue_trend",
                    new WidgetPosition(0, 0, 6, 4));

            // When & Then
            assertThatCode(widget::validate).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("equals 與 hashCode 測試")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("相同 widgetId 的 Widget 應相等")
        void sameWidgetId_shouldBeEqual() {
            // Given
            DashboardWidget w1 = DashboardWidget.create("w1", DashboardWidget.WidgetType.KPI_CARD, "標題A", "data_a", new WidgetPosition(0, 0, 3, 2));
            DashboardWidget w2 = DashboardWidget.create("w1", DashboardWidget.WidgetType.BAR_CHART, "標題B", "data_b", new WidgetPosition(3, 0, 3, 2));

            // Then
            assertThat(w1).isEqualTo(w2);
            assertThat(w1.hashCode()).isEqualTo(w2.hashCode());
        }

        @Test
        @DisplayName("不同 widgetId 的 Widget 不應相等")
        void differentWidgetId_shouldNotBeEqual() {
            // Given
            DashboardWidget w1 = DashboardWidget.create("w1", DashboardWidget.WidgetType.KPI_CARD, "標題", "data", new WidgetPosition(0, 0, 3, 2));
            DashboardWidget w2 = DashboardWidget.create("w2", DashboardWidget.WidgetType.KPI_CARD, "標題", "data", new WidgetPosition(0, 0, 3, 2));

            // Then
            assertThat(w1).isNotEqualTo(w2);
        }
    }

    @Nested
    @DisplayName("WidgetType 列舉")
    class WidgetTypeTests {

        @Test
        @DisplayName("應包含所有必要的 Widget 類型")
        void shouldContainAllTypes() {
            assertThat(DashboardWidget.WidgetType.values()).containsExactlyInAnyOrder(
                    DashboardWidget.WidgetType.KPI_CARD,
                    DashboardWidget.WidgetType.LINE_CHART,
                    DashboardWidget.WidgetType.BAR_CHART,
                    DashboardWidget.WidgetType.PIE_CHART,
                    DashboardWidget.WidgetType.TABLE,
                    DashboardWidget.WidgetType.GAUGE);
        }
    }
}
