package com.company.hrms.iam.application.service.user.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.iam.api.request.user.GetUserListRequest;

/**
 * 使用者查詢組裝器 (負責將 Request 轉換為 QueryGroup)
 */
@Component
public class UserQueryAssembler {

    /**
     * 轉換請求為查詢群組
     */
    public QueryGroup toQueryGroup(GetUserListRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 基礎過濾: 未刪除
        query.eq("is_deleted", 0);

        // 2. 狀態過濾
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            query.eq("status", request.getStatus());
        }

        // 3. 使用者名稱模糊查詢
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            query.like("username", request.getUsername());
        }

        // 4. 角色過濾
        if (request.getRoleId() != null && !request.getRoleId().isBlank()) {
            query.eq("roles.id", request.getRoleId());
        }

        // 5. 租戶過濾
        if (request.getTenantId() != null && !request.getTenantId().isBlank()) {
            query.eq("tenant_id", request.getTenantId());
        }

        return query;
    }
}
