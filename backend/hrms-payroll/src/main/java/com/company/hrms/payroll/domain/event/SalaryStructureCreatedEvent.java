package com.company.hrms.payroll.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 薪資結構已建立事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructureCreatedEvent {
    private String structureId;
    private String employeeId;
    private LocalDateTime effectiveDate;
    private LocalDateTime occurredAt;
}
