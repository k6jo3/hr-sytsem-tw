package com.company.hrms.notification.domain.repository;

import java.util.Set;

import com.company.hrms.notification.domain.model.aggregate.AnnouncementReadRecord;

/**
 * 公告閱讀記錄 Repository 介面
 *
 * @author Claude
 * @since 2026-01-30
 */
public interface IAnnouncementReadRecordRepository {

    AnnouncementReadRecord save(AnnouncementReadRecord record);

    boolean existsByAnnouncementIdAndEmployeeId(String announcementId, String employeeId);

    /**
     * 查詢員工已讀的公告 ID 列表
     *
     * @param employeeId 員工 ID
     * @return 已讀公告 ID 集合
     */
    Set<String> findReadAnnouncementIdsByEmployeeId(String employeeId);
}
