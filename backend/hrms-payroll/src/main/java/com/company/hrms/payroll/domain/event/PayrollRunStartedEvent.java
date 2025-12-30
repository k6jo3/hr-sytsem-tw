package com.company.hrms.payroll.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 薪資批次已開始計算事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRunStartedEvent extends DomainEvent {
    private String runId;
    private String organizationId;
    private LocalDateTime startedAt;

    @Override
    public String getAggregateId() {
        return runId;
    }

    @Override
    public String getAggregateType() {
        return "PayrollRun";
    }
}
