package com.company.hrms.attendance.api.response.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 審核補卡回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "審核補卡回應")
public class ApproveCorrectionResponse {

    @Schema(description = "補卡申請ID")
    private String correctionId;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "審核人")
    private String approvedBy;

    @Schema(description = "審核時間")
    private LocalDateTime approvedAt;
}
