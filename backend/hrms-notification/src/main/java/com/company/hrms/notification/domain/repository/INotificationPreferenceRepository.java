package com.company.hrms.notification.domain.repository;

import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.valueobject.PreferenceId;

import java.util.Optional;

/**
 * 通知偏好設定 Repository 介面
 * <p>
 * 定義通知偏好設定聚合根的持久化操作
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public interface INotificationPreferenceRepository {

    /**
     * 儲存偏好設定
     *
     * @param preference 偏好設定聚合根
     * @return 儲存後的偏好設定
     */
    NotificationPreference save(NotificationPreference preference);

    /**
     * 根據 ID 查詢偏好設定
     *
     * @param id 偏好設定 ID
     * @return Optional&lt;NotificationPreference&gt;
     */
    Optional<NotificationPreference> findById(PreferenceId id);

    /**
     * 根據員工 ID 查詢偏好設定
     *
     * @param employeeId 員工 ID
     * @return Optional&lt;NotificationPreference&gt;
     */
    Optional<NotificationPreference> findByEmployeeId(String employeeId);

    /**
     * 根據員工 ID 查詢偏好設定，若不存在則建立預設設定
     *
     * @param employeeId 員工 ID
     * @return NotificationPreference
     */
    NotificationPreference findByEmployeeIdOrCreateDefault(String employeeId);

    /**
     * 檢查員工是否已有偏好設定
     *
     * @param employeeId 員工 ID
     * @return true 表示已存在
     */
    boolean existsByEmployeeId(String employeeId);

    /**
     * 刪除偏好設定
     *
     * @param id 偏好設定 ID
     */
    void deleteById(PreferenceId id);
}
