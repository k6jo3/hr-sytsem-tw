package com.company.hrms.notification.infrastructure.persistence.assembler;

import com.company.hrms.common.querydsl.model.query.Operator;
import com.company.hrms.common.querydsl.model.query.QueryBuilder;
import com.company.hrms.common.querydsl.model.query.QueryGroup;
import com.company.hrms.notification.api.request.template.SearchTemplateRequest;
import org.springframework.stereotype.Component;

/**
 * 通知範本查詢條件組裝器
 * <p>
 * 負責將 Request DTO 轉換為 QueryGroup (Fluent-Query-Engine)
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Component
public class TemplateQueryAssembler {

    /**
     * 將查詢請求轉換為 QueryGroup
     * <p>
     * 包含以下查詢條件：
     * - 範本代碼 (LIKE)
     * - 範本名稱 (LIKE)
     * - 通知類型 (EQ)
     * - 狀態過濾 (EQ)
     * - 軟刪除過濾 (預設 isDeleted = false)
     * </p>
     *
     * @param request 查詢請求
     * @return QueryGroup
     */
    public QueryGroup toQueryGroup(SearchTemplateRequest request) {
        return QueryBuilder.where()
                .fromDto(request)  // 自動解析 @QueryFilter 註解
                .and("isDeleted", Operator.EQ, false)  // 預設過濾軟刪除
                .build();
    }

    /**
     * 查詢所有啟用的範本
     *
     * @return QueryGroup
     */
    public QueryGroup queryAllActive() {
        return QueryBuilder.where()
                .and("status", Operator.EQ, "ACTIVE")
                .and("isDeleted", Operator.EQ, false)
                .build();
    }

    /**
     * 根據範本代碼查詢
     *
     * @param templateCode 範本代碼
     * @return QueryGroup
     */
    public QueryGroup queryByTemplateCode(String templateCode) {
        return QueryBuilder.where()
                .and("templateCode", Operator.EQ, templateCode)
                .and("isDeleted", Operator.EQ, false)
                .build();
    }

    /**
     * 根據通知類型查詢
     *
     * @param notificationType 通知類型
     * @return QueryGroup
     */
    public QueryGroup queryByNotificationType(String notificationType) {
        return QueryBuilder.where()
                .and("notificationType", Operator.EQ, notificationType)
                .and("status", Operator.EQ, "ACTIVE")
                .and("isDeleted", Operator.EQ, false)
                .build();
    }
}
