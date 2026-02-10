package com.company.hrms.attendance.application.service.checkin.assembler;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.attendance.GetAttendanceListRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * 出勤查詢組裝器
 */
@Component
public class AttendanceQueryAssembler {

    public QueryGroup toQueryGroup(GetAttendanceListRequest request) {
        QueryBuilder builder = QueryBuilder.where();

        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            builder.and("employee_id", Operator.EQ, request.getEmployeeId());
        }

        if (request.getDate() != null) {
            builder.and("record_date", Operator.EQ, request.getDate());
        }

        if (request.getStartDate() != null) {
            builder.and("record_date", Operator.GTE, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            builder.and("record_date", Operator.LTE, request.getEndDate());
        }

        if (request.getMonth() != null && !request.getMonth().isBlank()) {
            String yearMonth = request.getMonth();
            LocalDate startDate = LocalDate.parse(yearMonth + "-01");
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            builder.and("record_date", Operator.GTE, startDate);
            builder.and("record_date", Operator.LTE, endDate);
        }

        if (request.getDeptId() != null && !request.getDeptId().isBlank()) {
            builder.and("department_id", Operator.EQ, request.getDeptId());
        }

        if (request.getLateFlag() != null) {
            builder.and("is_late", Operator.EQ, request.getLateFlag());
        }
        if (request.getEarlyLeaveFlag() != null) {
            builder.and("is_early_leave", Operator.EQ, request.getEarlyLeaveFlag());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            if ("ABNORMAL".equals(request.getStatus())) {
                builder.isNotNull("anomaly_type");
            } else {
                builder.and("status", Operator.EQ, request.getStatus());
            }
        }

        return builder.build();
    }
}
