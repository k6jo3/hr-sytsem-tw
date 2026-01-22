package com.company.hrms.training.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "拒絕報名請求")
public class RejectEnrollmentRequest {

    @Schema(description = "拒絕原因")
    @NotBlank(message = "拒絕原因不能為空")
    private String reason;
}
