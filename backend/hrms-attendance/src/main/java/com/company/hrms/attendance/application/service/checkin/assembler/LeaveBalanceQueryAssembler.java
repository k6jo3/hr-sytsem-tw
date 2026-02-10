package com.company.hrms.attendance.application.service.checkin.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.attendance.GetLeaveBalanceRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * 假別餘額查詢組裝器
 */
@Component
public class LeaveBalanceQueryAssembler {

    public QueryGroup toQueryGroup(GetLeaveBalanceRequest request) {
        QueryBuilder builder = QueryBuilder.where();

        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            builder.eq("employee_id", request.getEmployeeId());
        }
        if (request.getDeptId() != null && !request.getDeptId().isBlank()) {
            builder.and("employee_id", Operator.IN,
                    "(SELECT employee_id FROM employees WHERE department_id = '" + request.getDeptId() + "')");
        }
        if (request.getYear() != null) {
            builder.eq("year", request.getYear());
        }
        if (request.getLeaveType() != null && !request.getLeaveType().isBlank()) {
            builder.eq("leave_type_id",
                    "(SELECT leave_type_id FROM leave_types WHERE leave_code = '" + request.getLeaveType() + "')");
        }

        return builder.build();
    }
}
