package com.company.hrms.training.api.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "我的訓練時數統計")
public class MyTrainingHoursResponse {

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "總時數")
    private BigDecimal totalHours;

    @Schema(description = "年度時數")
    private BigDecimal yearToDateHours;
}
