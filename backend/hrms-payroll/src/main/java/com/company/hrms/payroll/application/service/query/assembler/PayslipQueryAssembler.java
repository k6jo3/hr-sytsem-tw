package com.company.hrms.payroll.application.service.query.assembler;

import java.time.LocalDate;
import java.time.YearMonth;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetPayslipListRequest;

/**
 * 薪資單查詢組裝器
 */
public class PayslipQueryAssembler {

    /**
     * 將查詢請求轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件群組
     */
    public QueryGroup toQueryGroup(GetPayslipListRequest request) {
        QueryBuilder builder = QueryBuilder.where().fromDto(request);

        // 年度月份過濾 (HR04 v2.2.1 PAY_QRY_P005)
        if (request.getYearMonth() != null && !request.getYearMonth().isEmpty()) {
            YearMonth ym = YearMonth.parse(request.getYearMonth());
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            builder.gte("periodStartDate", start);
            builder.lte("periodEndDate", end);
        }

        // 薪資單不進行軟刪除 (HR04 v2.0)，且沒有 is_deleted 欄位

        return builder.build();
    }
}
