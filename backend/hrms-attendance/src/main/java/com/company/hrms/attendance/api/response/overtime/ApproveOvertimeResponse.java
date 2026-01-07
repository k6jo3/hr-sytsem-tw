package com.company.hrms.attendance.api.response.overtime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 核准加班回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "核准加班回應")
public class ApproveOvertimeResponse {

    @Schema(description = "加班申請ID")
    private String overtimeId;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "核准人")
    private String approvedBy;

    @Schema(description = "核准時間")
    private LocalDateTime approvedAt;
}
