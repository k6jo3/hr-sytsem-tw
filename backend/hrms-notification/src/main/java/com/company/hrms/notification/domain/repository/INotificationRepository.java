package com.company.hrms.notification.domain.repository;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.valueobject.NotificationId;

import java.util.List;
import java.util.Optional;

/**
 * 通知 Repository 介面
 * <p>
 * 定義通知聚合根的持久化操作
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public interface INotificationRepository {

    /**
     * 儲存通知
     *
     * @param notification 通知聚合根
     * @return 儲存後的通知
     */
    Notification save(Notification notification);

    /**
     * 根據 ID 查詢通知
     *
     * @param id 通知 ID
     * @return Optional&lt;Notification&gt;
     */
    Optional<Notification> findById(NotificationId id);

    /**
     * 根據收件人 ID 查詢通知列表
     *
     * @param recipientId 收件人 ID
     * @return 通知列表
     */
    List<Notification> findByRecipientId(String recipientId);

    /**
     * 根據收件人 ID 查詢未讀通知列表
     *
     * @param recipientId 收件人 ID
     * @return 未讀通知列表
     */
    List<Notification> findUnreadByRecipientId(String recipientId);

    /**
     * 查詢收件人的未讀通知數量
     *
     * @param recipientId 收件人 ID
     * @return 未讀數量
     */
    long countUnreadByRecipientId(String recipientId);

    /**
     * 刪除通知
     *
     * @param id 通知 ID
     */
    void deleteById(NotificationId id);

    /**
     * 批次儲存通知
     *
     * @param notifications 通知列表
     * @return 儲存後的通知列表
     */
    List<Notification> saveAll(List<Notification> notifications);
}
