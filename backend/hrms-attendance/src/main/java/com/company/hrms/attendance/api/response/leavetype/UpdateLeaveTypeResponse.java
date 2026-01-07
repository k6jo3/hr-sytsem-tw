package com.company.hrms.attendance.api.response.leavetype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 更新假別回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新假別回應")
public class UpdateLeaveTypeResponse {

    @Schema(description = "假別ID")
    private String leaveTypeId;

    @Schema(description = "假別名稱")
    private String leaveTypeName;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}
