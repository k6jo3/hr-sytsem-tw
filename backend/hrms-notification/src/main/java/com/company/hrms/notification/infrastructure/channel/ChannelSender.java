package com.company.hrms.notification.infrastructure.channel;

import com.company.hrms.notification.domain.model.aggregate.Notification;

/**
 * 通知渠道發送器介面
 * <p>
 * 定義各渠道發送器的統一介面
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
public interface ChannelSender {

    /**
     * 發送通知
     *
     * @param notification 通知聚合根
     * @param recipientId  收件人 ID
     * @throws Exception 發送失敗時拋出例外
     */
    void send(Notification notification, String recipientId) throws Exception;

    /**
     * 取得渠道名稱
     *
     * @return 渠道名稱
     */
    String getChannelName();
}
