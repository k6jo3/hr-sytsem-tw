package com.company.hrms.organization.api.response.employee;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 員工升遷回應 DTO
 */
@Data
@Builder
public class PromoteEmployeeResponse {

    private String employeeId;
    private String employeeNumber;
    private String fullName;
    private String oldJobTitle;
    private String newJobTitle;
    private String oldJobLevel;
    private String newJobLevel;
    private LocalDate effectiveDate;
    private String message;

    public static PromoteEmployeeResponse success(String employeeId, String employeeNumber, String fullName,
                                                   String oldJobTitle, String newJobTitle,
                                                   String oldJobLevel, String newJobLevel,
                                                   LocalDate effectiveDate) {
        return PromoteEmployeeResponse.builder()
                .employeeId(employeeId)
                .employeeNumber(employeeNumber)
                .fullName(fullName)
                .oldJobTitle(oldJobTitle)
                .newJobTitle(newJobTitle)
                .oldJobLevel(oldJobLevel)
                .newJobLevel(newJobLevel)
                .effectiveDate(effectiveDate)
                .message("升遷成功")
                .build();
    }
}
