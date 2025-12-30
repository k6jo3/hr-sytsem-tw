package com.company.hrms.payroll.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 薪資結構已變更事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStructureChangedEvent {
    private String structureId;
    private String employeeId;
    private String reason;
    private LocalDateTime occurredAt;
}
