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

        // 1. 狀態
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            query.eq("status", request.getStatus());
        }

        // 2. 員工 ID
        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            query.eq("employee_id", request.getEmployeeId());
        }

        // 3. 加班類型
        if (request.getOvertimeType() != null && !request.getOvertimeType().isBlank()) {
            query.eq("overtime_type", request.getOvertimeType());
        }

        // 4. 部門
        if (request.getDeptId() != null && !request.getDeptId().isBlank()) {
            query.eq("department_id", request.getDeptId());
        }

        // 4. 日期
        if (request.getStartDate() != null) {
            query.gte("overtime_date", request.getStartDate());
        }

        if (request.getEndDate() != null) {
            query.lte("overtime_date", request.getEndDate());
        }

        return query;
    }
}
