package com.company.hrms.training.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "取消報名請求")
public class CancelEnrollmentRequest {

    @Schema(description = "取消原因")
    @NotBlank(message = "取消原因不能為空")
    private String reason;
}
