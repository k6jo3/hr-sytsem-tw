package com.company.hrms.attendance.api.response.leavetype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 建立假別回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "建立假別回應")
public class CreateLeaveTypeResponse {

    @Schema(description = "假別ID")
    private String leaveTypeId;

    @Schema(description = "假別代碼")
    private String leaveTypeCode;

    @Schema(description = "假別名稱")
    private String leaveTypeName;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}
