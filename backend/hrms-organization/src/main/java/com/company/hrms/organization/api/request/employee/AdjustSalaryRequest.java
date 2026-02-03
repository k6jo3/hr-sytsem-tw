package com.company.hrms.organization.api.request.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "員工調薪請求")
public class AdjustSalaryRequest {

    @Schema(description = "調整後薪資", example = "50000")
    @NotNull(message = "調整後薪資不可為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "薪資必須大於0")
    private BigDecimal newSalary;

    @Schema(description = "生效日期", example = "2023-11-01")
    @NotNull(message = "生效日期不可為空")
    private LocalDate effectiveDate;

    @Schema(description = "調薪原因", example = "年度績效考核優異")
    private String reason;
}
