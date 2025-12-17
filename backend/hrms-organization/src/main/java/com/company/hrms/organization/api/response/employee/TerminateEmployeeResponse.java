package com.company.hrms.organization.api.response.employee;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 員工離職回應 DTO
 */
@Data
@Builder
public class TerminateEmployeeResponse {

    private String employeeId;
    private String employeeNumber;
    private String fullName;
    private LocalDate terminationDate;
    private String message;

    public static TerminateEmployeeResponse success(String employeeId, String employeeNumber, String fullName,
                                                     LocalDate terminationDate) {
        return TerminateEmployeeResponse.builder()
                .employeeId(employeeId)
                .employeeNumber(employeeNumber)
                .fullName(fullName)
                .terminationDate(terminationDate)
                .message("離職處理成功")
                .build();
    }
}
