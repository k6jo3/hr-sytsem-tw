package com.company.hrms.attendance.api.response.monthclose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 執行月結回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "執行月結回應")
public class ExecuteMonthCloseResponse {

    @Schema(description = "月結批次ID")
    private String batchId;

    @Schema(description = "年份")
    private Integer year;

    @Schema(description = "月份")
    private Integer month;

    @Schema(description = "處理狀態", allowableValues = {"PROCESSING", "COMPLETED", "FAILED"})
    private String status;

    @Schema(description = "處理筆數")
    private Integer processedCount;

    @Schema(description = "成功筆數")
    private Integer successCount;

    @Schema(description = "失敗筆數")
    private Integer failedCount;

    @Schema(description = "執行時間")
    private LocalDateTime executedAt;
}
