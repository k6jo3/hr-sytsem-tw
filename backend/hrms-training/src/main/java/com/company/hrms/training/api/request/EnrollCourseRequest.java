package com.company.hrms.training.api.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "報名課程請求")
public class EnrollCourseRequest {

    @Schema(description = "課程ID")
    @NotBlank(message = "課程ID不能為空")
    private String courseId;

    @Schema(description = "員工ID")
    private String employeeId; // Optional, defaults to current user if not provided (e.g. admin enrolling for
                               // someone)

    @Schema(description = "報名原因")
    private String reason;

    @Schema(description = "備註")
    private String remarks;

    // Optional info for creation validation or pre-fill
    @Schema(description = "預計訓練時數")
    private BigDecimal trainingHours;

    @Schema(description = "預計費用")
    private BigDecimal cost;
}
