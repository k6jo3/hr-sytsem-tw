package com.company.hrms.attendance.api.response.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 補卡申請回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "補卡申請回應")
public class CreateCorrectionResponse {

    @Schema(description = "補卡申請ID")
    private String correctionId;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "簽核流程ID")
    private String workflowInstanceId;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}
