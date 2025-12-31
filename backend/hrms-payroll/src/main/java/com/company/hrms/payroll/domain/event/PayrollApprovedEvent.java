package com.company.hrms.payroll.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 薪資批次已核准事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollApprovedEvent {
    private String runId;
    private String approverId;
    private LocalDateTime approvedAt;
}
