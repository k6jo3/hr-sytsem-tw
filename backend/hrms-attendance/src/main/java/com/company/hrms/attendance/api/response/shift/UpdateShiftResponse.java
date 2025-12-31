package com.company.hrms.attendance.api.response.shift;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 更新班別回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新班別回應")
public class UpdateShiftResponse {

    @Schema(description = "班別ID")
    private String shiftId;

    @Schema(description = "班別名稱")
    private String shiftName;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}
