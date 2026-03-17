package com.company.hrms.timesheet.api.response;

import java.math.BigDecimal;
import java.util.List;

import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工時表列表查詢回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "工時表列表查詢回應")
public class GetTimesheetListResponse {

    /**
     * 工時表列表
     */
    @Schema(description = "工時表列表")
    private List<TimesheetItemDto> items;

    /**
     * 總筆數
     */
    @Schema(description = "總筆數")
    private long total;

    /**
     * 工時表摘要 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "工時表摘要項目")
    public static class TimesheetItemDto {

        @Schema(description = "工時表 ID")
        private String timesheetId;

        @Schema(description = "員工 ID")
        private String employeeId;

        @Schema(description = "週期起始日")
        private String periodStartDate;

        @Schema(description = "週期結束日")
        private String periodEndDate;

        @Schema(description = "總工時")
        private BigDecimal totalHours;

        @Schema(description = "工時表狀態")
        private TimesheetStatus status;

        @Schema(description = "提交時間")
        private String submittedAt;

        @Schema(description = "簽核時間")
        private String approvedAt;
    }
}
