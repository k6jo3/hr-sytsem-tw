package com.company.hrms.insurance.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量調整投保級距 Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAdjustLevelsRequest {

    /**
     * 保險類型清單（LABOR, HEALTH, PENSION）
     */
    @NotNull(message = "保險類型不可為空")
    private List<String> insuranceTypes;

    /**
     * 調整金額（如 -10000 表示每級下修1萬）
     */
    @NotNull(message = "調整金額不可為空")
    private BigDecimal adjustmentAmount;

    /**
     * 新級距生效日期
     */
    @NotNull(message = "生效日期不可為空")
    private LocalDate effectiveDate;

    /**
     * 新增最高級距的月薪（可選）
     */
    private BigDecimal newHighestLevelSalary;
}
