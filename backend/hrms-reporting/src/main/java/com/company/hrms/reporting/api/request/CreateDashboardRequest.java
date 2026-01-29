package com.company.hrms.reporting.api.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 建立儀表板請求
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Schema(description = "建立儀表板請求")
public class CreateDashboardRequest {

    @NotBlank(message = "儀表板名稱不可為空")
    @Size(max = 100, message = "儀表板名稱不可超過100字元")
    @Schema(description = "儀表板名稱", example = "高階主管儀表板")
    private String dashboardName;

    @Size(max = 500, message = "描述不可超過500字元")
    @Schema(description = "儀表板描述", example = "CEO 每日經營數據")
    private String description;

    @Schema(description = "是否公開", example = "false")
    private Boolean isPublic = false;

    @Schema(description = "Widget 配置列表")
    private List<WidgetConfigDto> widgets;

    /**
     * Widget 配置 DTO
     */
    @Data
    @Schema(description = "Widget 配置")
    public static class WidgetConfigDto {

        @Schema(description = "Widget ID", example = "w1")
        private String widgetId;

        @Schema(description = "Widget 類型", example = "KPI_CARD")
        private String widgetType;

        @Schema(description = "Widget 標題", example = "本月營收")
        private String title;

        @Schema(description = "資料來源", example = "monthly_revenue")
        private String dataSource;

        @Schema(description = "X 座標", example = "0")
        private Integer x;

        @Schema(description = "Y 座標", example = "0")
        private Integer y;

        @Schema(description = "寬度", example = "3")
        private Integer w;

        @Schema(description = "高度", example = "2")
        private Integer h;

        @Schema(description = "刷新間隔(秒)", example = "3600")
        private Integer refreshInterval;

        @Schema(description = "樣式配置(JSON)", example = "{\"color\":\"blue\"}")
        private String styleConfig;

        @Schema(description = "圖表配置(JSON)", example = "{\"type\":\"line\"}")
        private String chartConfig;
    }
}
