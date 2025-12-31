package com.company.hrms.attendance.application.service.overtime.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.overtime.GetOvertimeListRequest;
import com.company.hrms.common.query.QueryGroup;

/**
 * 加班查詢組裝器
 */
@Component
public class OvertimeQueryAssembler {

    public QueryGroup toQueryGroup(GetOvertimeListRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 基礎過濾: 未刪除
        query.eq("is_deleted", 0);

        // 2. 狀態
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            query.eq("status", request.getStatus());
        }

        // 3. 員工 ID
        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            query.eq("employee_id", request.getEmployeeId());
        }

        // 4. 加班類型
        if (request.getOvertimeType() != null && !request.getOvertimeType().isBlank()) {
            query.eq("overtime_type", request.getOvertimeType());
        }

        // 5. 部門
        if (request.getDeptId() != null && !request.getDeptId().isBlank()) {
            query.eq("department_id", request.getDeptId());
        }

        // 6. 日期 (大於等於)
        if (request.getStartDate() != null) {
            query.gte("overtime_date", request.getStartDate().toString());
        }

        if (request.getEndDate() != null) {
            query.lte("overtime_date", request.getEndDate().toString());
        }

        return query;
    }
}
