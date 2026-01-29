package com.company.hrms.performance.application.service.assembler;

import com.company.hrms.common.query.QueryBuilder;
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
        return QueryBuilder.where()
                .fromDto(request)
                .eq("is_deleted", 0)
                .build();
    }

    /**
     * GetMyReviewsRequest -> QueryGroup
     */
    public QueryGroup toQueryGroup(GetMyReviewsRequest request) {
        return QueryBuilder.where()
                .fromDto(request)
                .eq("is_deleted", 0)
                .build();
    }
}
