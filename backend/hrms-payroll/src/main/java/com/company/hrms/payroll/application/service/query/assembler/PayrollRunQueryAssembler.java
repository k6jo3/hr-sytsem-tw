package com.company.hrms.payroll.application.service.query.assembler;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetPayrollRunListRequest;

/**
 * 薪資批次查詢組裝器
 * 將 GetPayrollRunListRequest 轉換為 QueryGroup
 */
public class PayrollRunQueryAssembler {

    /**
     * 將查詢請求轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件群組
     */
    public QueryGroup toQueryGroup(GetPayrollRunListRequest request) {
        QueryGroup queryGroup = QueryGroup.and();

        // 組織過濾
        if (request.getOrganizationId() != null && !request.getOrganizationId().isEmpty()) {
            queryGroup.eq("organization_id", request.getOrganizationId());
        }

        // 狀態過濾
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            queryGroup.eq("status", request.getStatus());
        }

        // 日期範圍過濾
        if (request.getStartDate() != null) {
            queryGroup.gte("pay_period_start", request.getStartDate());
        }
        if (request.getEndDate() != null) {
            queryGroup.lte("pay_period_end", request.getEndDate());
        }

        // 軟刪除過濾 (始終添加)
        queryGroup.eq("is_deleted", 0);

        return queryGroup;
    }
}
