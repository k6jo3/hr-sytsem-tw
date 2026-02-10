package com.company.hrms.payroll.application.service.query.assembler;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetPayrollRunListRequest;

/**
 * 薪資批次查詢組裝器
 */
public class PayrollRunQueryAssembler {

    /**
     * 將查詢請求轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件群組
     */
    public QueryGroup toQueryGroup(GetPayrollRunListRequest request) {
        QueryBuilder builder = QueryBuilder.where().fromDto(request);

        // 軟刪除策略 (HR04 v2.0): 不使用 is_deleted，改用 status
        if (Boolean.TRUE.equals(request.getExcludeCancelled())) {
            builder.ne("status", "CANCELLED");
        }

        return builder.build();
    }
}
