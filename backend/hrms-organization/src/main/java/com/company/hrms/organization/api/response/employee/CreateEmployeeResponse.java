package com.company.hrms.organization.api.response.employee;

import lombok.Builder;
import lombok.Data;

/**
 * 新增員工回應 DTO
 */
@Data
@Builder
public class CreateEmployeeResponse {

    private String employeeId;
    private String employeeNumber;
    private String fullName;
    private String message;

    public static CreateEmployeeResponse success(String employeeId, String employeeNumber, String fullName) {
        return CreateEmployeeResponse.builder()
                .employeeId(employeeId)
                .employeeNumber(employeeNumber)
                .fullName(fullName)
                .message("員工建立成功")
                .build();
    }
}
