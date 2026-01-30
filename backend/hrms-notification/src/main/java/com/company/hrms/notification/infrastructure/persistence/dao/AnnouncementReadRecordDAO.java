package com.company.hrms.notification.infrastructure.persistence.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.company.hrms.notification.infrastructure.persistence.entity.AnnouncementReadRecordPO;

public interface AnnouncementReadRecordDAO extends JpaRepository<AnnouncementReadRecordPO, String> {

    boolean existsByAnnouncementIdAndEmployeeId(String announcementId, String employeeId);

    @Query("SELECT r.announcementId FROM AnnouncementReadRecordPO r WHERE r.employeeId = :employeeId")
    Set<String> findReadAnnouncementIdsByEmployeeId(@Param("employeeId") String employeeId);
}
