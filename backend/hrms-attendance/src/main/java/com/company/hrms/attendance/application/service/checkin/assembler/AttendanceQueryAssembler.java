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
        // 1. 使用 QueryBuilder 自動解析 DTO 上的 @QueryFilter / @EQ 註解
        QueryBuilder builder = QueryBuilder.where().fromDto(request);

        // 2. 月份查詢 (YYYY-MM) - 需手動處理範圍
        if (request.getMonth() != null && !request.getMonth().isBlank()) {
            String yearMonth = request.getMonth(); // 2025-01
            String start = yearMonth + "-01";
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            builder.and("date", Operator.GTE, startDate);
            builder.and("date", Operator.LTE, endDate);
        }

        // 3. 部門 ID - PO可能有缺失，暫保留手動加入 (假設 Entity 支援此欄位路徑或透過 Join)
        if (request.getDeptId() != null && !request.getDeptId().isBlank()) {
            builder.and("department_id", Operator.EQ, request.getDeptId());
        }

        return builder.build();
    }
}
