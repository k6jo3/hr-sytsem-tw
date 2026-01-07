package com.company.hrms.attendance.application.service.report.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.controller.report.HR03ReportQryController.MonthlyReportQueryRequest;
import com.company.hrms.attendance.api.controller.report.HR03ReportQryController.DailyReportQueryRequest;
import com.company.hrms.common.query.QueryGroup;

/**
 * 報表查詢組裝器
 */
@Component
public class ReportQueryAssembler {

    /**
     * 組裝月報表查詢條件
     */
    public QueryGroup toQueryGroup(MonthlyReportQueryRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 組織 ID (必填)
        query.eq("organization_id", request.organizationId());

        // 2. 年月
        if (request.year() != null && request.month() != null) {
            String yearMonth = String.format("%04d-%02d", request.year(), request.month());
            String startDate = yearMonth + "-01";
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

            query.gte("attendance_date", start.toString());
            query.lte("attendance_date", end.toString());
        }

        // 3. 部門 ID (選填)
        if (request.departmentId() != null && !request.departmentId().isBlank()) {
            query.eq("department_id", request.departmentId());
        }

        return query;
    }

    /**
     * 組裝日報表查詢條件
     */
    public QueryGroup toQueryGroup(DailyReportQueryRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 組織 ID (必填)
        query.eq("organization_id", request.organizationId());

        // 2. 日期 (必填)
        if (request.date() != null) {
            query.eq("attendance_date", request.date().toString());
        }

        // 3. 部門 ID (選填)
        if (request.departmentId() != null && !request.departmentId().isBlank()) {
            query.eq("department_id", request.departmentId());
        }

        return query;
    }
}
