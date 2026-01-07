package com.company.hrms.performance.application.service.assembler;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.api.request.GetCyclesRequest;
import com.company.hrms.performance.api.request.GetMyReviewsRequest;

/**
 * 績效管理查詢組裝器 (用於合約測試)
 */
public class PerformanceQueryAssembler {

    /**
     * GetCyclesRequest -> QueryGroup
     */
    public QueryGroup toQueryGroup(GetCyclesRequest request) {
        QueryGroup query = QueryGroup.and();

        if (request.getStatus() != null) {
            query.eq("status", request.getStatus().name());
        }

        if (request.getCycleType() != null) {
            query.eq("cycleType", request.getCycleType().name());
        }

        if (request.getYear() != null) {
            query.eq("year", request.getYear());
        }

        return query;
    }

    /**
     * GetMyReviewsRequest -> QueryGroup
     */
    public QueryGroup toQueryGroup(GetMyReviewsRequest request) {
        QueryGroup query = QueryGroup.and();

        if (request.getEmployeeId() != null && !request.getEmployeeId().isEmpty()) {
            query.eq("employeeId", request.getEmployeeId());
        }

        if (request.getStatus() != null) {
            query.eq("status", request.getStatus().name());
        }

        return query;
    }
}
