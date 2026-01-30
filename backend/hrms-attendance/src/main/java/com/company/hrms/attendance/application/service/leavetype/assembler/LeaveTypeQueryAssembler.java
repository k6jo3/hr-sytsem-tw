package com.company.hrms.attendance.application.service.leavetype.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.leavetype.GetLeaveTypeListRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * 假別查詢組裝器
 */
@Component
public class LeaveTypeQueryAssembler {

    public QueryGroup toQueryGroup(GetLeaveTypeListRequest request) {
        return QueryBuilder.where()
                .fromDto(request)
                .and("isDeleted", Operator.EQ, 0) // 基礎過濾: 未刪除
                .build();
    }
}
