package com.company.hrms.notification.domain.repository;

import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.model.valueobject.AnnouncementId;

import java.util.List;
import java.util.Optional;

/**
 * 公告 Repository 介面
 * <p>
 * 定義公告聚合根的持久化操作
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public interface IAnnouncementRepository {

    /**
     * 儲存公告
     *
     * @param announcement 公告聚合根
     * @return 儲存後的公告
     */
    Announcement save(Announcement announcement);

    /**
     * 根據 ID 查詢公告
     *
     * @param id 公告 ID
     * @return Optional&lt;Announcement&gt;
     */
    Optional<Announcement> findById(AnnouncementId id);

    /**
     * 查詢所有已發布且未過期的公告
     *
     * @return 公告列表
     */
    List<Announcement> findAllActiveAnnouncements();

    /**
     * 查詢所有公告（包含已過期）
     *
     * @return 公告列表
     */
    List<Announcement> findAll();

    /**
     * 查詢特定員工可見的公告
     * <p>
     * 根據目標對象過濾（ALL, DEPARTMENT, ROLE）
     * </p>
     *
     * @param employeeId   員工 ID
     * @param departmentId 員工部門 ID
     * @param roleIds      員工角色 ID 列表
     * @return 公告列表
     */
    List<Announcement> findVisibleAnnouncementsForEmployee(
            String employeeId,
            String departmentId,
            List<String> roleIds);

    /**
     * 刪除公告
     *
     * @param id 公告 ID
     */
    void deleteById(AnnouncementId id);

    /**
     * 查詢特定發布者的公告
     *
     * @param publishedBy 發布者 ID
     * @return 公告列表
     */
    List<Announcement> findByPublishedBy(String publishedBy);
}
