package com.company.hrms.payroll.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 薪資批次計算完成事件
 * 用於通知後續處理 (如報表生成)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRunCompletedEvent {
    private String runId;
    private String organizationId;
    private int totalEmployees;
    private LocalDateTime completedAt;
}
