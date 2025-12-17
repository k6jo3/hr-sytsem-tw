package com.company.hrms.organization.api.request.employee;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 員工離職請求 DTO
 */
@Data
public class TerminateEmployeeRequest {

    @NotNull(message = "離職日期不可為空")
    private LocalDate terminationDate;

    private String reason;
}
