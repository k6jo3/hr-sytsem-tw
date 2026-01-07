package com.company.hrms.performance.application.service.context;

import java.time.LocalDate;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleType;

import lombok.Getter;
import lombok.Setter;

/**
 * 建立考核週期 Pipeline Context
 */
@Getter
@Setter
public class CreateCycleContext extends PipelineContext {

    // === 輸入 ===
    private final String cycleName;
    private final CycleType cycleType; // 使用 enum 而非 String
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate selfEvalDeadline;
    private final LocalDate managerEvalDeadline;

    // === 中間資料 ===
    private PerformanceCycle cycle;

    // === 輸出 ===
    private String cycleId;

    public CreateCycleContext(String cycleName, CycleType cycleType, LocalDate startDate,
            LocalDate endDate, LocalDate selfEvalDeadline, LocalDate managerEvalDeadline) {
        this.cycleName = cycleName;
        this.cycleType = cycleType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.selfEvalDeadline = selfEvalDeadline;
        this.managerEvalDeadline = managerEvalDeadline;
    }
}
