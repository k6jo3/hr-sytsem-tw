package com.company.hrms.payroll.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 薪資單已產生事件
 * 可用於觸發 PDF 生成
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayslipGeneratedEvent {
    private String payslipId;
    private String employeeId;
    private String runId;
    private LocalDateTime occurredAt;
}
