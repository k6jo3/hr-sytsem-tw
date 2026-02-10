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
        QueryBuilder builder = QueryBuilder.where();

        // 假別狀態
        if (request.getIsActive() != null) {
            builder.and("is_active", Operator.EQ, request.getIsActive());
        } else {
            // 預設查詢啟用的假別 (依合約 ATT_QRY_T001)
            builder.and("is_active", Operator.EQ, true);
        }

        // 組織 ID
        if (request.getOrganizationId() != null && !request.getOrganizationId().isBlank()) {
            builder.and("organization_id", Operator.EQ, request.getOrganizationId());
        }

        // 是否支薪
        if (request.getIsPaid() != null) {
            builder.and("is_paid", Operator.EQ, request.getIsPaid());
        }

        return builder.build();
    }
}
