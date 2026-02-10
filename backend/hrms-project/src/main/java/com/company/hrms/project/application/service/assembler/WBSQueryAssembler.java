package com.company.hrms.project.application.service.assembler;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.api.request.GetWBSListRequest;

/**
 * WBS 查詢組裝器
 * 將 Request 轉換為 QueryGroup 供合約測試驗證
 */
public class WBSQueryAssembler {

    /**
     * 將 GetWBSListRequest 轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件組
     */
    public QueryGroup toQueryGroup(GetWBSListRequest request) {
        QueryGroup query = QueryGroup.and();

        // Project filter
        if (request.getProjectId() != null && !request.getProjectId().isEmpty()) {
            query.eq("project_id", request.getProjectId());
        }

        // Parent filter
        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            query.eq("parent_id", request.getParentId());
        }

        // Top level filter (parent_id IS NULL)
        if (request.getIsTopLevel() != null && request.getIsTopLevel()) {
            query.isNull("parent_id");
        }

        // Status filter
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            query.eq("status", request.getStatus());
        }

        // Delayed filter
        if (request.getIsDelayed() != null && request.getIsDelayed()) {
            query.eq("is_delayed", 1);
        }

        // Owner filter
        if (request.getOwnerId() != null && !request.getOwnerId().isEmpty()) {
            query.eq("owner_id", request.getOwnerId());
        }

        // 軟刪除過濾
        query.eq("is_deleted", 0);

        return query;
    }
}
