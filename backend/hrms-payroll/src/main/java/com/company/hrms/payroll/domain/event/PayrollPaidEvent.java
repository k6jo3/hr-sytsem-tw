package com.company.hrms.payroll.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 薪資批次已發放事件
 * 可用於觸發通知
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollPaidEvent {
    private String runId;
    private java.time.LocalDate payDate;
    private LocalDateTime occurredAt;
}
