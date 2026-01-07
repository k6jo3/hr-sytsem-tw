package com.company.hrms.attendance.api.response.shift;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 停用班別回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "停用班別回應")
public class DeactivateShiftResponse {

    @Schema(description = "班別ID")
    private String shiftId;

    @Schema(description = "是否啟用")
    private Boolean isActive;

    @Schema(description = "停用時間")
    private LocalDateTime deactivatedAt;

    @Schema(description = "受影響的員工數")
    private Integer affectedEmployeeCount;
}
