package com.company.hrms.payroll.application.service.query.assembler;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetPayslipListRequest;

/**
 * 薪資單查詢組裝器
 * 將 GetPayslipListRequest 轉換為 QueryGroup
 */
public class PayslipQueryAssembler {

    /**
     * 將查詢請求轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件群組
     */
    public QueryGroup toQueryGroup(GetPayslipListRequest request) {
        QueryGroup queryGroup = QueryGroup.and();

        // 薪資批次過濾
        if (request.getRunId() != null && !request.getRunId().isEmpty()) {
            queryGroup.eq("payroll_run_id", request.getRunId());
        }

        // 員工過濾
        if (request.getEmployeeId() != null && !request.getEmployeeId().isEmpty()) {
            queryGroup.eq("employee_id", request.getEmployeeId());
        }

        // 軟刪除過濾 (始終添加)
        queryGroup.eq("is_deleted", 0);

        return queryGroup;
    }
}
