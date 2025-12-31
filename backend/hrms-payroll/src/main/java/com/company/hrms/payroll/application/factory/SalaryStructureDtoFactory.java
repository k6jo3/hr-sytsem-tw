package com.company.hrms.payroll.application.factory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.company.hrms.payroll.application.dto.response.SalaryItemResponse;
import com.company.hrms.payroll.application.dto.response.SalaryStructureResponse;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.entity.SalaryItem;

public class SalaryStructureDtoFactory {

    public static SalaryStructureResponse toResponse(SalaryStructure domain) {
        if (domain == null) {
            return null;
        }

        List<SalaryItemResponse> items = domain.getItems() == null ? Collections.emptyList()
                : domain.getItems().stream()
                        .map(SalaryStructureDtoFactory::toItemResponse)
                        .collect(Collectors.toList());

        return SalaryStructureResponse.builder()
                .id(domain.getId().getValue())
                .employeeId(domain.getEmployeeId())
                .payrollSystem(domain.getPayrollSystem().name())
                .payrollCycle(domain.getPayrollCycle().name())
                .monthlySalary(domain.getMonthlySalary())
                .hourlyRate(domain.getHourlyRate())
                .calculatedHourlyRate(domain.getCalculatedHourlyRate())
                .effectiveDate(domain.getEffectiveDate())
                .endDate(domain.getEndDate())
                .active(domain.isActive())
                .items(items)
                .build();
    }

    public static SalaryItemResponse toItemResponse(SalaryItem domain) {
        if (domain == null) {
            return null;
        }
        return SalaryItemResponse.builder()
                .itemId(domain.getItemId())
                .code(domain.getItemCode())
                .name(domain.getItemName())
                .type(domain.getItemType().name())
                .amount(domain.getAmount())
                .fixedAmount(domain.isFixedAmount())
                .taxable(domain.isTaxable())
                .insurable(domain.isInsurable())
                .build();
    }
}
