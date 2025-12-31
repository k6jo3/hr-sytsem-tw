package com.company.hrms.insurance.api.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投保級距回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceLevelResponse {

    private String levelId;
    private String insuranceType;
    private int levelNumber;
    private BigDecimal monthlySalary;
    private String effectiveDate;
    private String endDate;
    private boolean isActive;
}
