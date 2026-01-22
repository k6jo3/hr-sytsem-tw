package com.company.hrms.training.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "關閉課程請求")
public class CloseCourseRequest {

    @Schema(description = "關閉原因")
    @NotBlank(message = "關閉原因不能為空")
    private String reason;
}
