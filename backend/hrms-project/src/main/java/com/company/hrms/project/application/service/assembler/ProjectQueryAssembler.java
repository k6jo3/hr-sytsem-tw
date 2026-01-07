package com.company.hrms.project.application.service.assembler;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.api.request.GetProjectListRequest;

/**
 * 專案查詢組裝器
 * 將 Request 轉換為 QueryGroup 供合約測試驗證
 */
public class ProjectQueryAssembler {

    /**
     * 將 GetProjectListRequest 轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件組
     */
    public QueryGroup toQueryGroup(GetProjectListRequest request) {
        QueryGroup query = QueryGroup.and();

        // Status filter
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            query.eq("status", request.getStatus());
        }

        // Keyword filter (name OR code)
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            query.addSubGroup(
                    QueryGroup.or()
                            .like("projectName", "%" + request.getKeyword() + "%")
                            .like("projectCode", "%" + request.getKeyword() + "%"));
        }

        return query;
    }
}
