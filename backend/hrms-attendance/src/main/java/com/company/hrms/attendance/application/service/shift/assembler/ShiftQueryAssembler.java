package com.company.hrms.attendance.application.service.shift.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.controller.shift.HR03ShiftQryController.ShiftQueryRequest;
import com.company.hrms.common.query.QueryGroup;

/**
 * 班別查詢組裝器
 */
@Component
public class ShiftQueryAssembler {

    public QueryGroup toQueryGroup(ShiftQueryRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 基礎過濾: 未刪除
        query.eq("is_deleted", 0);

        // 2. 組織 ID
        if (request.organizationId() != null && !request.organizationId().isBlank()) {
            query.eq("organization_id", request.organizationId());
        }

        // 3. 班別類型
        if (request.shiftType() != null && !request.shiftType().isBlank()) {
            query.eq("shift_type", request.shiftType());
        }

        // 4. 是否啟用
        if (request.isActive() != null) {
            query.eq("is_active", request.isActive() ? 1 : 0);
        }

        return query;
    }
}
