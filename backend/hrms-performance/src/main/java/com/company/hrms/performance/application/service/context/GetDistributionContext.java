package com.company.hrms.performance.application.service.context;

import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.performance.api.response.GetDistributionResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;

import lombok.Getter;
import lombok.Setter;

/**
 * 查詢績效分布 Pipeline Context
 */
@Getter
@Setter
public class GetDistributionContext extends PipelineContext {

    // === 輸入 ===
    private final String cycleId;

    // === 中間資料 ===
    /**
     * 該週期的所有已完成考核
     */
    private List<PerformanceReview> reviews;

    // === 輸出 ===
    /**
     * 分布統計結果
     */
    private GetDistributionResponse response;

    public GetDistributionContext(String cycleId) {
        this.cycleId = cycleId;
    }
}
