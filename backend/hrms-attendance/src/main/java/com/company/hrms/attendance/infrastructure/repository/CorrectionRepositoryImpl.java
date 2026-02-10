package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.CorrectionApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionId;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionType;
import com.company.hrms.attendance.domain.repository.ICorrectionRepository;
import com.company.hrms.attendance.infrastructure.po.AttendanceCorrectionPO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CorrectionRepositoryImpl extends BaseRepository<AttendanceCorrectionPO, String>
        implements ICorrectionRepository {

    public CorrectionRepositoryImpl(JPAQueryFactory factory) {
        super(factory, AttendanceCorrectionPO.class);
    }

    @Override
    public void save(CorrectionApplication application) {
        AttendanceCorrectionPO po = toPO(application);
        if (super.findById(po.getId()).isPresent()) {
            po.setUpdatedAt(LocalDateTime.now());
            super.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            super.save(po);
        }
    }

    @Override
    public Optional<CorrectionApplication> findById(CorrectionId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public java.util.List<CorrectionApplication> findByQuery(com.company.hrms.common.query.QueryGroup query) {
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    private CorrectionApplication toDomain(AttendanceCorrectionPO po) {
        return CorrectionApplication.reconstitute(
                new CorrectionId(po.getId()),
                po.getEmployeeId(),
                po.getAttendanceRecordId(),
                po.getCorrectionDate(),
                CorrectionType.valueOf(po.getCorrectionType()),
                po.getCorrectedCheckInTime(),
                po.getCorrectedCheckOutTime(),
                po.getReason(),
                ApplicationStatus.valueOf(po.getStatus()),
                po.getRejectionReason());
    }

    private AttendanceCorrectionPO toPO(CorrectionApplication domain) {
        return AttendanceCorrectionPO.builder()
                .id(domain.getId().getValue())
                .employeeId(domain.getEmployeeId())
                .attendanceRecordId(domain.getAttendanceRecordId())
                .correctionDate(domain.getCorrectionDate())
                .correctionType(domain.getCorrectionType().name())
                .correctedCheckInTime(domain.getCorrectedCheckInTime())
                .correctedCheckOutTime(domain.getCorrectedCheckOutTime())
                .reason(domain.getReason())
                .status(domain.getStatus().name())
                .rejectionReason(domain.getRejectionReason())
                .build();
    }
}
