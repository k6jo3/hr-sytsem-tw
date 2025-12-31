package com.company.hrms.attendance.application.service.checkin.assembler;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.attendance.GetAttendanceListRequest;
import com.company.hrms.common.query.QueryGroup;

/**
 * 出勤查詢組裝器
 */
@Component
public class AttendanceQueryAssembler {

    public QueryGroup toQueryGroup(GetAttendanceListRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 基礎過濾
        // AttendanceRecord 可能沒有軟刪除，暫不加 is_deleted

        // 2. 員工 ID
        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            query.eq("employee_id", request.getEmployeeId());
        }

        // 3. 日期 (單日)
        if (request.getDate() != null) {
            query.eq("attendance_date", request.getDate().toString());
        }

        // 4. 月份查詢 (YYYY-MM)
        if (request.getMonth() != null && !request.getMonth().isBlank()) {
            String yearMonth = request.getMonth(); // 2025-01
            String start = yearMonth + "-01";
            LocalDate startDate = java.time.LocalDate.parse(start);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            query.gte("attendance_date", startDate.toString());
            query.lte("attendance_date", endDate.toString());
        }

        // 5. 部門 ID
        if (request.getDeptId() != null && !request.getDeptId().isBlank()) {
            query.eq("department_id", request.getDeptId());
        }

        // 6. 狀態
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            query.eq("status", request.getStatus());
        }

        // 7. 遲到
        if (request.getLateFlag() != null && request.getLateFlag()) {
            query.eq("late_flag", 1);
        }

        // 8. 早退
        if (request.getEarlyLeaveFlag() != null && request.getEarlyLeaveFlag()) {
            query.eq("early_leave_flag", 1);
        }

        return query;
    }
}
