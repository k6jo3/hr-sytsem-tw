package com.company.hrms.attendance.application.service.leave.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.leave.GetLeaveListRequest;
import com.company.hrms.common.query.QueryGroup;

/**
 * 請假查詢組裝器
 */
@Component
public class LeaveQueryAssembler {

    public QueryGroup toQueryGroup(GetLeaveListRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 狀態
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            query.eq("status", request.getStatus());
        }

        // 2. 員工 ID
        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            query.eq("employee_id", request.getEmployeeId());
        }

        // 3. 假別
        if (request.getLeaveType() != null && !request.getLeaveType().isBlank()) {
            query.eq("leave_type_id",
                    "(SELECT leave_type_id FROM leave_types WHERE leave_code = '" + request.getLeaveType() + "')");
        }

        // 4. 部門
        if (request.getDeptId() != null && !request.getDeptId().isBlank()) {
            query.eq("department_id", request.getDeptId());
        }

        // 5. 日期範圍 (重疊查詢)
        if (request.getStartDate() != null) {
            query.gte("end_date", request.getStartDate().toString());
        }
        if (request.getEndDate() != null) {
            query.lte("start_date", request.getEndDate().toString());
        }

        return query;
    }
}
