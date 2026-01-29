package com.company.hrms.reporting.domain.model.dashboard;

import com.company.hrms.common.domain.model.ValueObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Dashboard Widget 值物件
 * 
 * <p>
 * 代表儀表板上的一個 Widget 元件
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWidget extends ValueObject {

    private String widgetId;
    private WidgetType widgetType;
    private String title;
    private String dataSource;
    private WidgetPosition position;
    private Integer refreshInterval; // 刷新間隔(秒)
    private String styleConfig; // JSON 格式的樣式配置
    private String chartConfig; // JSON 格式的圖表配置

    /**
     * Widget 類型枚舉
     */
    public enum WidgetType {
        KPI_CARD, // KPI 指標卡片
        LINE_CHART, // 折線圖
        BAR_CHART, // 長條圖
        PIE_CHART, // 圓餅圖
        TABLE, // 表格
        GAUGE // 儀表板
    }

    /**
     * 建立 Widget
     */
    public static DashboardWidget create(
            String widgetId,
            WidgetType widgetType,
            String title,
            String dataSource,
            WidgetPosition position) {

        return new DashboardWidget(
                widgetId,
                widgetType,
                title,
                dataSource,
                position,
                3600, // 預設 1 小時刷新
                null,
                null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DashboardWidget that = (DashboardWidget) o;
        return java.util.Objects.equals(widgetId, that.widgetId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(widgetId);
    }

    @Override
    public String toString() {
        return "DashboardWidget{" +
                "widgetId='" + widgetId + '\'' +
                ", widgetType=" + widgetType +
                ", title='" + title + '\'' +
                "}";
    }

    /**
     * 驗證 Widget 配置
     */
    public void validate() {
        if (widgetId == null || widgetId.trim().isEmpty()) {
            throw new IllegalArgumentException("Widget ID 不可為空");
        }

        if (widgetType == null) {
            throw new IllegalArgumentException("Widget 類型不可為空");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Widget 標題不可為空");
        }

        if (dataSource == null || dataSource.trim().isEmpty()) {
            throw new IllegalArgumentException("資料來源不可為空");
        }

        if (position == null) {
            throw new IllegalArgumentException("Widget 位置不可為空");
        }

        position.validate();
    }
}
