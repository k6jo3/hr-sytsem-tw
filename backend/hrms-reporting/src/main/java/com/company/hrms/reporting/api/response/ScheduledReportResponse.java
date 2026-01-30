package com.company.hrms.reporting.api.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "排程報表列表回應")
public class ScheduledReportResponse {

    @Schema(description = "排程列表")
    private List<ScheduledReportItem> content;

    @Schema(description = "總筆數")
    private long totalElements;

    @Schema(description = "總頁數")
    private int totalPages;

    @Data
    @Builder
    @Schema(description = "排程項目")
    public static class ScheduledReportItem {
        @Schema(description = "排程ID")
        private String scheduleId;

        @Schema(description = "排程名稱")
        private String scheduleName;

        @Schema(description = "報表類型")
        private String reportType;

        @Schema(description = "排程表達式")
        private String cronExpression;

        @Schema(description = "下次執行時間")
        private LocalDateTime nextRunTime;

        @Schema(description = "是否啟用")
        private Boolean isEnabled;
    }
}
