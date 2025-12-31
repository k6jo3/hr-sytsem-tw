package com.company.hrms.iam.application.service.permission.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.iam.api.request.permission.GetPermissionListRequest;

/**
 * 權限查詢組裝器 (負責將 Request 轉換為 QueryGroup)
 */
@Component
public class PermissionQueryAssembler {

    /**
     * 轉換請求為查詢群組
     */
    public QueryGroup toQueryGroup(GetPermissionListRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 基礎過濾: 未刪除
        query.eq("is_deleted", 0);

        // 2. 模組過濾
        if (request.getModule() != null && !request.getModule().isBlank()) {
            query.eq("module", request.getModule());
        }

        // 3. 權限類型過濾
        if (request.getType() != null && !request.getType().isBlank()) {
            query.eq("type", request.getType());
        }

        // 4. 角色關聯過濾
        if (request.getRoleId() != null && !request.getRoleId().isBlank()) {
            query.eq("roles.id", request.getRoleId());
        }

        return query;
    }
}
