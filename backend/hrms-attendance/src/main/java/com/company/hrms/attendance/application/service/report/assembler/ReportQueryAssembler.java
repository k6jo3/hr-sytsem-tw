package com.company.hrms.attendance.application.service.report.assembler;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.request.report.GetDailyReportRequest;
import com.company.hrms.attendance.api.request.report.GetMonthlyReportRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * 報表查詢組裝器
 */
@Component
public class ReportQueryAssembler {

    public QueryGroup toQueryGroup(GetMonthlyReportRequest request) {
        var builder = QueryBuilder.where();

        if (request.getOrganizationId() != null) {
            builder.and("organization_id", Operator.EQ, request.getOrganizationId());
        }
        if (request.getDepartmentId() != null) {
            builder.and("employee_id", Operator.IN,
                    "(SELECT employee_id FROM employees WHERE department_id = '" + request.getDepartmentId() + "')");
        }

        // 處理年份與月份轉換為日期區間 (合約要求 record_date)
        if (request.getYear() != null && request.getMonth() != null) {
            LocalDate start = LocalDate.of(request.getYear(), request.getMonth(), 1);
            LocalDate endDate = start.withDayOfMonth(start.lengthOfMonth());

            builder.and("record_date", Operator.GTE, start)
                    .and("record_date", Operator.LTE, endDate);
        }

        return builder.build();
    }

    public QueryGroup toQueryGroup(GetDailyReportRequest request) {
        var builder = QueryBuilder.where();

        if (request.getOrganizationId() != null) {
            builder.and("organization_id", Operator.EQ, request.getOrganizationId());
        }
        if (request.getDepartmentId() != null) {
            builder.and("employee_id", Operator.IN,
                    "(SELECT employee_id FROM employees WHERE department_id = '" + request.getDepartmentId() + "')");
        }
        if (request.getDate() != null) {
            builder.and("record_date", Operator.EQ, request.getDate());
        }

        return builder.build();
    }
}
