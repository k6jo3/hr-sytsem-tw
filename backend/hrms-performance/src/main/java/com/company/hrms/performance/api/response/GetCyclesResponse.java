package com.company.hrms.performance.api.response;

import java.time.LocalDate;
import java.util.List;

import com.company.hrms.performance.domain.model.valueobject.CycleStatus;
import com.company.hrms.performance.domain.model.valueobject.CycleType;
import com.company.hrms.performance.domain.model.valueobject.EvaluationTemplate;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 考核週期列表回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCyclesResponse {
    private List<CycleSummary> cycles;
    private int totalCount;
    private int pageSize;
    private int currentPage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CycleSummary {
        private String cycleId;
        private String cycleName;
        private CycleType cycleType;
        private CycleStatus status;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean hasTemplate;
        /** 詳情查詢時回傳完整 template（列表查詢時為 null） */
        private EvaluationTemplate template;
    }
}
