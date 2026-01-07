package com.company.hrms.attendance.application.service.leavetype.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.controller.leavetype.HR03LeaveTypeQryController.LeaveTypeQueryRequest;
import com.company.hrms.common.query.QueryGroup;

/**
 * 假別查詢組裝器
 */
@Component
public class LeaveTypeQueryAssembler {

    public QueryGroup toQueryGroup(LeaveTypeQueryRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 基礎過濾: 未刪除
        query.eq("is_deleted", 0);

        // 2. 組織 ID
        if (request.organizationId() != null && !request.organizationId().isBlank()) {
            query.eq("organization_id", request.organizationId());
        }

        // 3. 是否支薪
        if (request.isPaid() != null) {
            query.eq("is_paid", request.isPaid() ? 1 : 0);
        }

        // 4. 是否啟用
        if (request.isActive() != null) {
            query.eq("is_active", request.isActive() ? 1 : 0);
        }

        return query;
    }
}
