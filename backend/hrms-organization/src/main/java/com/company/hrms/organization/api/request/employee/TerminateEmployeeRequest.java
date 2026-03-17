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

    /**
     * 離職類型（必填）
     * 可選值：VOLUNTARY_RESIGNATION, LAYOFF, DISMISSAL, MUTUAL_AGREEMENT, CONTRACT_EXPIRY, RETIREMENT
     */
    @NotNull(message = "離職類型不可為空")
    private String terminationType;
}
