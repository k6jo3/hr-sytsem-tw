package com.company.hrms.insurance.api.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加退保記錄詳情回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDetailResponse {

    private String enrollmentId;
    private String employeeId;
    private String employeeName;
    private String insuranceType;
    private String insuranceTypeDisplay;
    private String status;
    private String statusDisplay;
    private String enrollDate;
    private String withdrawDate;
    private BigDecimal monthlySalary;
    private Integer levelNumber;
    private String insuranceUnitName;
}
