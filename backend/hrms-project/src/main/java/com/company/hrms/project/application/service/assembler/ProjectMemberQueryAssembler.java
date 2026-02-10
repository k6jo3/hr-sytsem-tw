package com.company.hrms.project.application.service.assembler;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.api.request.GetProjectMemberListRequest;

/**
 * 專案成員查詢組裝器
 * 將 Request 轉換為 QueryGroup 供合約測試驗證
 */
public class ProjectMemberQueryAssembler {

    /**
     * 將 GetProjectMemberListRequest 轉換為 QueryGroup
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件組
     */
    public QueryGroup toQueryGroup(GetProjectMemberListRequest request) {
        QueryGroup query = QueryGroup.and();

        // Project filter
        if (request.getProjectId() != null && !request.getProjectId().isEmpty()) {
            query.eq("project_id", request.getProjectId());
        }

        // Role filter
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            query.eq("role", request.getRole());
        }

        // Status filter
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            query.eq("status", request.getStatus());
        }

        // Employee ID filter
        if (request.getEmployeeId() != null && !request.getEmployeeId().isEmpty()) {
            query.eq("employee_id", request.getEmployeeId());
        }

        // 軟刪除過濾
        query.eq("is_deleted", 0);

        return query;
    }
}
