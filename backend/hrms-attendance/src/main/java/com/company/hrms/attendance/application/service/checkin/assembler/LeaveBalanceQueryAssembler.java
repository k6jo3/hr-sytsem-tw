package com.company.hrms.attendance.application.service.checkin.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.attendance.GetLeaveBalanceRequest;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * 假別餘額查詢組裝器
 */
@Component
public class LeaveBalanceQueryAssembler {

    public QueryGroup toQueryGroup(GetLeaveBalanceRequest request) {
        // 使用 QueryBuilder 自動解析 DTO 上的 @EQ 註解
        return QueryBuilder.where().fromDto(request).build();
    }
}
