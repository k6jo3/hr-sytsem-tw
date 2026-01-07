package com.company.hrms.performance.application.factory;

import com.company.hrms.performance.api.response.GetCyclesResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;

/**
 * 考核週期 DTO Factory
 */
public class CycleDtoFactory {

    public static GetCyclesResponse.CycleSummary toSummary(PerformanceCycle cycle) {
        return GetCyclesResponse.CycleSummary.builder()
                .cycleId(cycle.getCycleId().getValue().toString())
                .cycleName(cycle.getCycleName())
                .cycleType(cycle.getCycleType())
                .status(cycle.getStatus())
                .startDate(cycle.getStartDate())
                .endDate(cycle.getEndDate())
                .hasTemplate(cycle.getTemplate() != null)
                .build();
    }
}
