package com.company.hrms.attendance.application.service.shift.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.shift.GetShiftListRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * 班別查詢組裝器
 * 符合 Fluent-Query-Engine 的設計
 */
@Component
public class ShiftQueryAssembler {

    public QueryGroup toQueryGroup(GetShiftListRequest request) {
        return QueryBuilder.where()
                .fromDto(request)
                .and("isDeleted", Operator.EQ, 0)
                .build();
    }
}
