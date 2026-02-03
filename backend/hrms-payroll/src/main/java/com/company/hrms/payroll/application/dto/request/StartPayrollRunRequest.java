package com.company.hrms.payroll.application.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartPayrollRunRequest {
    @NotBlank(message = "薪資制度為必填")
    private String payrollSystem; // MONTHLY, HOURLY

    @NotBlank(message = "組織 ID 為必填")
    private String organizationId;

    @NotNull(message = "開始日期為必填")
    private LocalDate startDate;

    @NotNull(message = "結束日期為必填")
    private LocalDate endDate;

    @NotNull(message = "發薪日為必填")
    private LocalDate payDate;

    @NotBlank(message = "批次名稱為必填")
    private String name;
}
