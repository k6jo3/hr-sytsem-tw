package com.company.hrms.project.application.service.assembler;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.api.request.GetProjectCostListRequest;

/**
 * 專案成本查詢組裝器
 * 將 Request 轉換為 QueryGroup 供合約測試驗證
 */
public class ProjectCostQueryAssembler {

    /**
     * 將 GetProjectCostListRequest 轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件組
     */
    public QueryGroup toQueryGroup(GetProjectCostListRequest request) {
        QueryGroup query = QueryGroup.and();

        // Project filter
        if (request.getProjectId() != null && !request.getProjectId().isEmpty()) {
            query.eq("project_id", request.getProjectId());
        }

        // Cost type filter
        if (request.getCostType() != null && !request.getCostType().isEmpty()) {
            query.eq("cost_type", request.getCostType());
        }

        // Year month filter
        if (request.getYearMonth() != null && !request.getYearMonth().isEmpty()) {
            query.eq("year_month", request.getYearMonth());
        }

        // Over budget filter
        // Over budget filter
        // 跨字段比較 (actual_amount > budget_amount)
        if (request.getIsOverBudget() != null && request.getIsOverBudget()) {
            query.gt("actual_amount", "budget_amount");
        }

        return query;
    }
}
