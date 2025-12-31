package com.company.hrms.attendance.api.response.leavetype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 停用假別回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "停用假別回應")
public class DeactivateLeaveTypeResponse {

    @Schema(description = "假別ID")
    private String leaveTypeId;

    @Schema(description = "是否啟用")
    private Boolean isActive;

    @Schema(description = "停用時間")
    private LocalDateTime deactivatedAt;
}
