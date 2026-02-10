package com.company.hrms.attendance.api.response.overtime;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 駁回加班回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "駁回加班回應")
public class RejectOvertimeResponse {

    @Schema(description = "加班申請ID")
    private String overtimeId;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "駁回人")
    private String rejectedBy;

    @Schema(description = "駁回時間")
    private LocalDateTime rejectedAt;

    @Schema(description = "駁回原因")
    private String rejectionReason;

    public static RejectOvertimeResponse success(String overtimeId) {
        return RejectOvertimeResponse.builder()
                .overtimeId(overtimeId)
                .status("REJECTED")
                .rejectedAt(LocalDateTime.now())
                .build();
    }
}
