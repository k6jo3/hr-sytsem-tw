package com.company.hrms.notification.infrastructure.persistence.assembler;

import com.company.hrms.common.query.QueryGroup;
// TODO: Phase 5 - 取消註解此行
// import com.company.hrms.notification.api.request.notification.SearchNotificationRequest;
import org.springframework.stereotype.Component;

/**
 * 通知查詢條件組裝器
 * <p>
 * 負責將 Request DTO 轉換為 QueryGroup (Fluent-Query-Engine)
 * </p>
 * <p>
 * 採用宣告式查詢模式（參考 HR03 Attendance）：
 * - 使用 QueryGroup.and() 作為起點
 * - 使用流暢方法鏈 (query.eq(), query.gte(), query.isNull() 等)
 * - 條件式添加過濾器
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Component
public class NotificationQueryAssembler {

    // TODO: Phase 5 - 取消註解此方法（需要 SearchNotificationRequest）
    /*
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
    /*
    public QueryGroup toQueryGroup(SearchNotificationRequest request) {
        QueryGroup query = QueryGroup.and();

        // 基礎過濾：未刪除
        query.eq("is_deleted", 0);

        // 收件人 ID
        if (request.getRecipientId() != null && !request.getRecipientId().isBlank()) {
            query.eq("recipient_id", request.getRecipientId());
        }

        // 狀態
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            query.eq("status", request.getStatus());
        }

        // 通知類型
        if (request.getNotificationType() != null && !request.getNotificationType().isBlank()) {
            query.eq("notification_type", request.getNotificationType());
        }

        // 優先級
        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            query.eq("priority", request.getPriority());
        }

        // 時間範圍
        if (request.getStartDate() != null) {
            query.gte("created_at", request.getStartDate().toString());
        }
        if (request.getEndDate() != null) {
            query.lte("created_at", request.getEndDate().toString());
        }

        return query;
    }
    */

    /**
     * 查詢特定收件人的未讀通知
     * <p>
     * 注意：通知 PO 使用 read_at 欄位判斷是否已讀 (NULL = 未讀)
     * </p>
     *
     * @param recipientId 收件人 ID
     * @return QueryGroup
     */
    public QueryGroup queryUnreadByRecipient(String recipientId) {
        QueryGroup query = QueryGroup.and();

        query.eq("recipient_id", recipientId);
        query.isNull("read_at");
        query.eq("is_deleted", 0);

        return query;
    }

    /**
     * 查詢特定收件人的所有通知
     *
     * @param recipientId 收件人 ID
     * @return QueryGroup
     */
    public QueryGroup queryByRecipient(String recipientId) {
        QueryGroup query = QueryGroup.and();

        query.eq("recipient_id", recipientId);
        query.eq("is_deleted", 0);

        return query;
    }

    /**
     * 查詢特定收件人的已讀通知
     * <p>
     * 注意：通知 PO 使用 read_at 欄位判斷是否已讀 (NOT NULL = 已讀)
     * </p>
     *
     * @param recipientId 收件人 ID
     * @return QueryGroup
     */
    public QueryGroup queryReadByRecipient(String recipientId) {
        QueryGroup query = QueryGroup.and();

        query.eq("recipient_id", recipientId);
        query.isNotNull("read_at");
        query.eq("is_deleted", 0);

        return query;
    }

    /**
     * 依類型查詢特定收件人的通知
     *
     * @param recipientId      收件人 ID
     * @param notificationType 通知類型
     * @return QueryGroup
     */
    public QueryGroup queryByRecipientAndType(String recipientId, String notificationType) {
        QueryGroup query = QueryGroup.and();

        query.eq("recipient_id", recipientId);
        query.eq("notification_type", notificationType);
        query.eq("is_deleted", 0);

        return query;
    }

    /**
     * 依優先級查詢特定收件人的通知
     *
     * @param recipientId 收件人 ID
     * @param priority    優先級
     * @return QueryGroup
     */
    public QueryGroup queryByRecipientAndPriority(String recipientId, String priority) {
        QueryGroup query = QueryGroup.and();

        query.eq("recipient_id", recipientId);
        query.eq("priority", priority);
        query.eq("is_deleted", 0);

        return query;
    }

    /**
     * 查詢特定收件人最近 N 天的通知
     *
     * @param recipientId 收件人 ID
     * @param sinceDate   起始日期
     * @return QueryGroup
     */
    public QueryGroup queryRecentByRecipient(String recipientId, String sinceDate) {
        QueryGroup query = QueryGroup.and();

        query.eq("recipient_id", recipientId);
        query.gte("created_at", sinceDate);
        query.eq("is_deleted", 0);

        return query;
    }

    /**
     * HR 查詢全部通知（無收件人限制）
     *
     * @return QueryGroup
     */
    public QueryGroup queryAllNotifications() {
        QueryGroup query = QueryGroup.and();

        query.eq("is_deleted", 0);

        return query;
    }

    /**
     * 查詢特定狀態的通知
     *
     * @param status 狀態
     * @return QueryGroup
     */
    public QueryGroup queryByStatus(String status) {
        QueryGroup query = QueryGroup.and();

        query.eq("status", status);
        query.eq("is_deleted", 0);

        return query;
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
        QueryGroup query = QueryGroup.and();

        query.eq("recipient_id", recipientId);
        query.eq("is_deleted", 0);

        // OR 條件：未讀或高優先級
        QueryGroup orCondition = QueryGroup.or();
        orCondition.isNull("read_at");
        orCondition.eq("priority", "HIGH");

        query.addSubGroup(orCondition);

        return query;
    }

    /**
     * 依通知渠道查詢
     * <p>
     * 注意：channels 欄位是 JSON 格式儲存，使用 LIKE 查詢包含特定渠道的通知
     * </p>
     *
     * @param channel 通知渠道 (如 EMAIL, PUSH, TEAMS)
     * @return QueryGroup
     */
    public QueryGroup queryByChannel(String channel) {
        QueryGroup query = QueryGroup.and();

        query.like("channels", channel);
        query.eq("is_deleted", 0);

        return query;
    }

    /**
     * 依發送日期範圍查詢
     * <p>
     * 查詢從指定日期開始的已發送通知
     * </p>
     *
     * @param sinceDate 起始日期 (YYYY-MM-DD)
     * @return QueryGroup
     */
    public QueryGroup querySentSinceDate(String sinceDate) {
        QueryGroup query = QueryGroup.and();

        query.gte("sent_at", sinceDate);
        query.eq("is_deleted", 0);

        return query;
    }
}
