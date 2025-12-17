package com.company.hrms.organization.api.request.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 員工調動請求 DTO
 */
@Data
public class TransferEmployeeRequest {

    @NotBlank(message = "新部門ID不可為空")
    private String newDepartmentId;

    private String newManagerId;

    @NotNull(message = "生效日期不可為空")
    private LocalDate effectiveDate;

    private String reason;
}
