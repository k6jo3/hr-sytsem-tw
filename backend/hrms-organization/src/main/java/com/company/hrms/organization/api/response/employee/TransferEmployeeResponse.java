package com.company.hrms.organization.api.response.employee;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 員工調動回應 DTO
 */
@Data
@Builder
public class TransferEmployeeResponse {

    private String employeeId;
    private String employeeNumber;
    private String fullName;
    private String oldDepartmentName;
    private String newDepartmentName;
    private LocalDate effectiveDate;
    private String message;

    public static TransferEmployeeResponse success(String employeeId, String employeeNumber, String fullName,
                                                    String oldDepartmentName, String newDepartmentName,
                                                    LocalDate effectiveDate) {
        return TransferEmployeeResponse.builder()
                .employeeId(employeeId)
                .employeeNumber(employeeNumber)
                .fullName(fullName)
                .oldDepartmentName(oldDepartmentName)
                .newDepartmentName(newDepartmentName)
                .effectiveDate(effectiveDate)
                .message("部門調動成功")
                .build();
    }
}
