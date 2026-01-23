package com.company.hrms.notification.domain.repository;

import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.TemplateId;

import java.util.List;
import java.util.Optional;

/**
 * 通知範本 Repository 介面
 * <p>
 * 定義通知範本聚合根的持久化操作
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public interface INotificationTemplateRepository {

    /**
     * 儲存範本
     *
     * @param template 範本聚合根
     * @return 儲存後的範本
     */
    NotificationTemplate save(NotificationTemplate template);

    /**
     * 根據 ID 查詢範本
     *
     * @param id 範本 ID
     * @return Optional&lt;NotificationTemplate&gt;
     */
    Optional<NotificationTemplate> findById(TemplateId id);

    /**
     * 根據範本代碼查詢範本
     *
     * @param templateCode 範本代碼
     * @return Optional&lt;NotificationTemplate&gt;
     */
    Optional<NotificationTemplate> findByTemplateCode(String templateCode);

    /**
     * 查詢所有啟用的範本
     *
     * @return 範本列表
     */
    List<NotificationTemplate> findAllActive();

    /**
     * 查詢所有範本
     *
     * @return 範本列表
     */
    List<NotificationTemplate> findAll();

    /**
     * 檢查範本代碼是否已存在
     *
     * @param templateCode 範本代碼
     * @return true 表示已存在
     */
    boolean existsByTemplateCode(String templateCode);

    /**
     * 刪除範本
     *
     * @param id 範本 ID
     */
    void deleteById(TemplateId id);
}
