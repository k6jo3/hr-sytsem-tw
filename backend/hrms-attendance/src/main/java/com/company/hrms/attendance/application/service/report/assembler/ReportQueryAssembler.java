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
 * 符合 Fluent-Query-Engine 的設計，支援由 DTO 自動構建查詢
 */
@Component
public class ReportQueryAssembler {

    /**
     * 組裝月報表查詢條件
     */
    public QueryGroup toQueryGroup(GetMonthlyReportRequest request) {
        var builder = QueryBuilder.where().fromDto(request);

        // 處理年份與月份轉換為日期區間
        if (request.getYear() != null && request.getMonth() != null) {
            LocalDate start = LocalDate.of(request.getYear(), request.getMonth(), 1);
            LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

            builder.and("date", Operator.GTE, start)
                    .and("date", Operator.LTE, end);
        }

        return builder.build();
    }

    /**
     * 組裝日報表查詢條件
     */
    public QueryGroup toQueryGroup(GetDailyReportRequest request) {
        return QueryBuilder.where()
                .fromDto(request)
                .build();
    }
}
