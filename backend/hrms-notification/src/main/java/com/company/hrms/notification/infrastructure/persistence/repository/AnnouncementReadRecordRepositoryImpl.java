package com.company.hrms.notification.infrastructure.persistence.repository;

import java.util.Set;

import org.springframework.stereotype.Repository;

import com.company.hrms.notification.domain.model.aggregate.AnnouncementReadRecord;
import com.company.hrms.notification.domain.repository.IAnnouncementReadRecordRepository;
import com.company.hrms.notification.infrastructure.persistence.dao.AnnouncementReadRecordDAO;
import com.company.hrms.notification.infrastructure.persistence.entity.AnnouncementReadRecordPO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AnnouncementReadRecordRepositoryImpl implements IAnnouncementReadRecordRepository {

    private final AnnouncementReadRecordDAO dao;

    @Override
    public AnnouncementReadRecord save(AnnouncementReadRecord record) {
        AnnouncementReadRecordPO po = AnnouncementReadRecordPO.builder()
                .id(record.getId().getValue())
                .announcementId(record.getAnnouncementId())
                .employeeId(record.getEmployeeId())
                .readAt(record.getReadAt())
                .build();

        dao.save(po);
        return record;
    }

    @Override
    public boolean existsByAnnouncementIdAndEmployeeId(String announcementId, String employeeId) {
        return dao.existsByAnnouncementIdAndEmployeeId(announcementId, employeeId);
    }

    @Override
    public Set<String> findReadAnnouncementIdsByEmployeeId(String employeeId) {
        return dao.findReadAnnouncementIdsByEmployeeId(employeeId);
    }
}
