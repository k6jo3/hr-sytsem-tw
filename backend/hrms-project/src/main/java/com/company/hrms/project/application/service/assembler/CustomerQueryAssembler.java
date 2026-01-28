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

        // Keyword filter (name OR code OR taxId)
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            query.addSubGroup(
                    QueryGroup.or()
                            .like("name", request.getKeyword())
                            .like("code", request.getKeyword())
                            .like("tax_id", request.getKeyword()));
        }

        // 軟刪除過濾
        query.eq("is_deleted", 0);

        return query;
    }
}
