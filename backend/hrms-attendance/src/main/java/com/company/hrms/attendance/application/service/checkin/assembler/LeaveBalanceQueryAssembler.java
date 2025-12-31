package com.company.hrms.attendance.application.service.checkin.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.attendance.GetLeaveBalanceRequest;
import com.company.hrms.common.query.QueryGroup;

/**
 * 假別餘額查詢組裝器
 */
@Component
public class LeaveBalanceQueryAssembler {

    public QueryGroup toQueryGroup(GetLeaveBalanceRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 員工 ID
        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            query.eq("employee_id", request.getEmployeeId());
        }

        // 2. 年度
        if (request.getYear() != null) {
            query.eq("year", request.getYear());
        }

        // 3. 假別
        if (request.getLeaveType() != null && !request.getLeaveType().isBlank()) {
            query.eq("leave_type", request.getLeaveType());
        }

        // 4. 部門
        if (request.getDeptId() != null && !request.getDeptId().isBlank()) {
            query.eq("department_id", request.getDeptId());
        }

        return query;
    }
}
