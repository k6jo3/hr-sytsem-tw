package com.company.hrms.project.application.service.assembler;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.api.request.GetCustomerListRequest;

/**
 * 客戶查詢組裝器
 * 將 Request 轉換為 QueryGroup 供合約測試驗證
 */
public class CustomerQueryAssembler {

    /**
     * 將 GetCustomerListRequest 轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件組
     */
    public QueryGroup toQueryGroup(GetCustomerListRequest request) {
        QueryGroup query = QueryGroup.and();

        // Status filter
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            query.eq("status", request.getStatus());
        }

        // Industry filter
        if (request.getIndustry() != null && !request.getIndustry().isEmpty()) {
            query.eq("industry", request.getIndustry());
        }

        // 有專案客戶過濾
        if (request.getHasProjects() != null && request.getHasProjects()) {
            query.gt("project_count", 0);
        }

        // 負責業務過濾
        if (request.getSalesRepId() != null && !request.getSalesRepId().isEmpty()) {
            query.eq("sales_rep_id", request.getSalesRepId());
        }

        // 關鍵字過濾 (客戶名稱 OR 客戶代碼 OR 統一編號)
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            query.addSubGroup(
                    QueryGroup.or()
                            .like("customer_name", request.getKeyword())
                            .like("customer_code", request.getKeyword())
                            .like("tax_id", request.getKeyword()));
        }

        // 軟刪除過濾
        query.eq("is_deleted", 0);

        return query;
    }
}
