package com.company.hrms.attendance.api.request.leave;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 駁回請假請求 DTO
 */
@Data
@Schema(description = "駁回請假請求")
public class RejectLeaveRequest {

    @NotBlank(message = "駁回原因不可為空")
    @Size(min = 1, max = 500, message = "駁回原因長度需在1-500字元之間")
    @Schema(description = "駁回原因")
    private String reason;
}
