package com.company.hrms.attendance.application.service.correction.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.controller.attendance.HR03CheckInQryController.CorrectionQueryRequest;
import com.company.hrms.common.query.QueryGroup;

/**
 * 補卡申請查詢組裝器
 */
@Component
public class CorrectionQueryAssembler {

    public QueryGroup toQueryGroup(CorrectionQueryRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 員工 ID
        if (request.employeeId() != null && !request.employeeId().isBlank()) {
            query.eq("employee_id", request.employeeId());
        }

        // 2. 狀態
        if (request.status() != null && !request.status().isBlank()) {
            query.eq("status", request.status());
        }

        // 3. 日期範圍 (合約指定使用 created_at)
        if (request.startDate() != null) {
            query.gte("created_at", request.startDate().toString());
        }

        if (request.endDate() != null) {
            query.lte("created_at", request.endDate().toString());
        }

        return query;
    }
}
