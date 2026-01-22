package com.company.hrms.training.api.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "完成訓練請求")
public class CompleteTrainingRequest {

    @Schema(description = "完成時數")
    @NotNull(message = "完成時數不能為空")
    private BigDecimal completedHours;

    @Schema(description = "成績/分數")
    private BigDecimal score;

    @Schema(description = "是否通過")
    @NotNull(message = "是否通過不能為空")
    private Boolean passed;

    @Schema(description = "評語/回饋")
    private String feedback;
}
