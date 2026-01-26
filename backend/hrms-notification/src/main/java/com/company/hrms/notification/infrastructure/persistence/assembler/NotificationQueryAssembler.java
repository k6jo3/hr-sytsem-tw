package com.company.hrms.notification.infrastructure.persistence.assembler;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * 通知查詢組裝器
 * <p>
 * 負責將業務查詢需求轉換為 QueryGroup
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
public class NotificationQueryAssembler {

    public QueryGroup queryByRecipient(String recipientId) {
        return QueryBuilder.where()
                .eq("recipient_id", recipientId)
                .eq("is_deleted", 0)
                .build();
    }

    public QueryGroup queryUnreadByRecipient(String recipientId) {
        return QueryBuilder.where()
                .eq("recipient_id", recipientId)
                .and("read_at", Operator.IS_NULL, null)
                .eq("is_deleted", 0)
                .build();
    }

    public QueryGroup queryReadByRecipient(String recipientId) {
        return QueryBuilder.where()
                .eq("recipient_id", recipientId)
                .and("read_at", Operator.IS_NOT_NULL, null)
                .eq("is_deleted", 0)
                .build();
    }

    public QueryGroup queryByRecipientAndType(String recipientId, String type) {
        return QueryBuilder.where()
                .eq("recipient_id", recipientId)
                .eq("notification_type", type)
                .eq("is_deleted", 0)
                .build();
    }

    public QueryGroup queryByRecipientAndPriority(String recipientId, String priority) {
        return QueryBuilder.where()
                .eq("recipient_id", recipientId)
                .eq("priority", priority)
                .eq("is_deleted", 0)
                .build();
    }

    public QueryGroup queryRecentByRecipient(String recipientId, String dateStr) {
        return QueryBuilder.where()
                .eq("recipient_id", recipientId)
                .and("created_at", Operator.GTE, dateStr)
                .eq("is_deleted", 0)
                .build();
    }

    public QueryGroup queryAllNotifications() {
        return QueryBuilder.where()
                .eq("is_deleted", 0)
                .build();
    }

    public QueryGroup queryByStatus(String status) {
        return QueryBuilder.where()
                .eq("status", status)
                .eq("is_deleted", 0)
                .build();
    }

    public QueryGroup queryByChannel(String channel) {
        return QueryBuilder.where()
                .and("channels", Operator.LIKE, "%" + channel + "%")
                .eq("is_deleted", 0)
                .build();
    }

    public QueryGroup querySentSinceDate(String dateStr) {
        return QueryBuilder.where()
                .and("sent_at", Operator.GTE, dateStr)
                .eq("is_deleted", 0)
                .build();
    }
}
