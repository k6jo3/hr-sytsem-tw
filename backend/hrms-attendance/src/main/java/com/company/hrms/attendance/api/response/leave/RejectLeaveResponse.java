package com.company.hrms.attendance.api.response.leave;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 駁回請假回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "駁回請假回應")
public class RejectLeaveResponse {

    @Schema(description = "申請ID")
    private String applicationId;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "駁回人")
    private String rejectedBy;

    @Schema(description = "駁回時間")
    private LocalDateTime rejectedAt;

    @Schema(description = "駁回原因")
    private String rejectionReason;
}
