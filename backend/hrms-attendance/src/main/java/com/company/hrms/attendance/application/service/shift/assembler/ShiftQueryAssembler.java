package com.company.hrms.attendance.application.service.shift.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.shift.GetShiftListRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * 班別查詢組裝器
 */
@Component
public class ShiftQueryAssembler {

    public QueryGroup toQueryGroup(GetShiftListRequest request) {
        QueryBuilder builder = QueryBuilder.where();

        // 班別狀態（ShiftPO.isActive 為 Integer 型別，需傳入 1/0）
        if (request.getIsActive() != null) {
            builder.and("isActive", Operator.EQ, request.getIsActive() ? 1 : 0);
        } else {
            // 預設查詢啟用的班別 (依合約 ATT_QRY_S001)
            builder.and("isActive", Operator.EQ, 1);
        }

        // 組織 ID
        if (request.getOrganizationId() != null && !request.getOrganizationId().isBlank()) {
            builder.and("organizationId", Operator.EQ, request.getOrganizationId());
        }

        // 班別類型
        if (request.getShiftType() != null && !request.getShiftType().isBlank()) {
            builder.and("type", Operator.EQ, request.getShiftType());
        }

        return builder.build();
    }
}
