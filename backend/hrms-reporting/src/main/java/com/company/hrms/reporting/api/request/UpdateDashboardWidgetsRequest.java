package com.company.hrms.reporting.api.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新 Widget 配置請求
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Schema(description = "更新 Widget 配置請求")
public class UpdateDashboardWidgetsRequest {

    @NotBlank(message = "儀表板ID不可為空")
    @Schema(description = "儀表板ID")
    private String dashboardId;

    @Schema(description = "Widget 配置列表")
    private List<WidgetConfigDto> widgets;

    @Data
    @Schema(description = "Widget 配置")
    public static class WidgetConfigDto {
        @Schema(description = "Widget ID")
        private String widgetId;

        @Schema(description = "Widget 類型")
        private String widgetType;

        @Schema(description = "Widget 標題")
        private String title;

        @Schema(description = "資料來源")
        private String dataSource;

        @Schema(description = "X 座標")
        private Integer x;

        @Schema(description = "Y 座標")
        private Integer y;

        @Schema(description = "寬度")
        private Integer w;

        @Schema(description = "高度")
        private Integer h;

        @Schema(description = "刷新間隔(秒)")
        private Integer refreshInterval;

        @Schema(description = "樣式配置(JSON)")
        private String styleConfig;

        @Schema(description = "圖表配置(JSON)")
        private String chartConfig;
    }
}
