package com.company.hrms.iam.application.service.role.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.iam.api.request.role.GetRoleListRequest;

/**
 * 角色查詢組裝器 (負責將 Request 轉換為 QueryGroup)
 */
@Component
public class RoleQueryAssembler {

    /**
     * 轉換請求為查詢群組
     */
    public QueryGroup toQueryGroup(GetRoleListRequest request) {
        QueryGroup query = QueryGroup.and();

        // 1. 基礎過濾: 未刪除
        query.eq("is_deleted", 0);

        // 2. 狀態過濾
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            query.eq("status", request.getStatus());
        }

        // 3. 角色名稱模糊查詢
        if (request.getName() != null && !request.getName().isBlank()) {
            query.like("name", request.getName());
        }

        // 4. 角色類型過濾
        if (request.getType() != null && !request.getType().isBlank()) {
            query.eq("type", request.getType());
        }

        // 5. 租戶過濾
        if (request.getTenantId() != null && !request.getTenantId().isBlank()) {
            query.eq("tenant_id", request.getTenantId());
        }

        return query;
    }
}
