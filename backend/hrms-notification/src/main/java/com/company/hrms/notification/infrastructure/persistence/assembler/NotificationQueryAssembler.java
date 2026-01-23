package com.company.hrms.notification.infrastructure.persistence.assembler;

import com.company.hrms.common.querydsl.model.query.FilterUnit;
import com.company.hrms.common.querydsl.model.query.Operator;
import com.company.hrms.common.querydsl.model.query.QueryBuilder;
import com.company.hrms.common.querydsl.model.query.QueryGroup;
import com.company.hrms.notification.api.request.notification.SearchNotificationRequest;
import org.springframework.stereotype.Component;

/**
 * 通知查詢條件組裝器
 * <p>
 * 負責將 Request DTO 轉換為 QueryGroup (Fluent-Query-Engine)
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Component
public class NotificationQueryAssembler {

    /**
     * 將查詢請求轉換為 QueryGroup
     * <p>
     * 包含以下查詢條件：
     * - 收件人 ID (必須)
     * - 狀態過濾
     * - 類型過濾
     * - 優先級過濾
     * - 時間範圍過濾
     * - 軟刪除過濾 (預設 isDeleted = false)
     * </p>
     *
     * @param request 查詢請求
     * @return QueryGroup
     */
    public QueryGroup toQueryGroup(SearchNotificationRequest request) {
        return QueryBuilder.where()
                .fromDto(request)  // 自動解析 @QueryFilter 註解
                .and("isDeleted", Operator.EQ, false)  // 預設過濾軟刪除
                .build();
    }

    /**
     * 查詢特定收件人的未讀通知
     *
     * @param recipientId 收件人 ID
     * @return QueryGroup
     */
    public QueryGroup queryUnreadByRecipient(String recipientId) {
        return QueryBuilder.where()
                .and("recipientId", Operator.EQ, recipientId)
                .and("status", Operator.EQ, "READ")
                .and("isDeleted", Operator.EQ, false)
                .build();
    }

    /**
     * 查詢特定收件人的所有通知
     *
     * @param recipientId 收件人 ID
     * @return QueryGroup
     */
    public QueryGroup queryByRecipient(String recipientId) {
        return QueryBuilder.where()
                .and("recipientId", Operator.EQ, recipientId)
                .and("isDeleted", Operator.EQ, false)
                .build();
    }

    /**
     * 查詢特定狀態的通知
     *
     * @param status 狀態
     * @return QueryGroup
     */
    public QueryGroup queryByStatus(String status) {
        return QueryBuilder.where()
                .and("status", Operator.EQ, status)
                .and("isDeleted", Operator.EQ, false)
                .build();
    }

    /**
     * 組合複雜查詢條件
     * <p>
     * 示例：查詢特定收件人的「未讀」或「高優先級」通知
     * </p>
     *
     * @param recipientId 收件人 ID
     * @return QueryGroup
     */
    public QueryGroup queryUrgentOrUnread(String recipientId) {
        return QueryBuilder.where()
                .and("recipientId", Operator.EQ, recipientId)
                .and("isDeleted", Operator.EQ, false)
                .orGroup(sub -> sub
                        .and("status", Operator.EQ, "PENDING")
                        .and("priority", Operator.EQ, "HIGH")
                )
                .build();
    }
}
