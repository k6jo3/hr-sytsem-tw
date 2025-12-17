package com.company.hrms.organization.api.request.employee;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 員工升遷請求 DTO
 */
@Data
public class PromoteEmployeeRequest {

    private String newJobTitle;
    private String newJobLevel;

    @NotNull(message = "生效日期不可為空")
    private LocalDate effectiveDate;

    private String reason;
}
