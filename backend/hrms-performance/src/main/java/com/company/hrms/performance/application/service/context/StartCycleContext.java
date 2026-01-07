package com.company.hrms.performance.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;

import lombok.Getter;
import lombok.Setter;

/**
 * 啟動考核週期 Pipeline Context
 */
@Getter
@Setter
public class StartCycleContext extends PipelineContext {

    // === 輸入 ===
    /**
     * 週期ID
     */
    private final String cycleId;

    // === 中間資料 ===
    /**
     * 載入的考核週期
     */
    private PerformanceCycle cycle;

    /**
     * 建構子
     */
    public StartCycleContext(String cycleId) {
        this.cycleId = cycleId;
    }
}
