package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.valueobject.AnomalyType;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.attendance.infrastructure.po.AttendanceRecordPO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class AttendanceRecordRepositoryImpl extends BaseRepository<AttendanceRecordPO, String>
        implements IAttendanceRecordRepository {

    public AttendanceRecordRepositoryImpl(JPAQueryFactory factory) {
        super(factory, AttendanceRecordPO.class);
    }

    @Override
    public Optional<AttendanceRecord> findById(RecordId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<AttendanceRecord> findByEmployeeIdAndDate(String employeeId, LocalDate date) {
        QueryGroup query = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .and("recordDate", Operator.EQ, date)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<AttendanceRecord> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate,
            LocalDate endDate) {
        QueryGroup query = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .and("recordDate", Operator.GTE, startDate)
                .and("recordDate", Operator.LTE, endDate)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<AttendanceRecord> findByQuery(QueryGroup query) {
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AttendanceRecord> findPageByQuery(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    @Override
    public void save(AttendanceRecord record) {
        AttendanceRecordPO po = toPO(record);
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
    public void delete(RecordId id) {
        super.deleteById(id.getValue());
    }

    private AttendanceRecord toDomain(AttendanceRecordPO po) {
        return AttendanceRecord.reconstitute(
                new RecordId(po.getId()),
                po.getEmployeeId(),
                po.getRecordDate(),
                po.getShiftId(),
                po.getCheckInTime(),
                po.getCheckOutTime(),
                po.getIsLate() != null ? po.getIsLate() : false,
                po.getLateMinutes() != null ? po.getLateMinutes() : 0,
                po.getIsEarlyLeave() != null ? po.getIsEarlyLeave() : false,
                po.getEarlyLeaveMinutes() != null ? po.getEarlyLeaveMinutes() : 0,
                po.getAnomalyType() != null ? AnomalyType.valueOf(po.getAnomalyType()) : AnomalyType.NORMAL,
                po.getIsCorrected() != null ? po.getIsCorrected() : false);
    }

    private AttendanceRecordPO toPO(AttendanceRecord record) {
        return AttendanceRecordPO.builder()
                .id(record.getId().getValue())
                .employeeId(record.getEmployeeId())
                .recordDate(record.getDate())
                .shiftId(record.getShiftId())
                .checkInTime(record.getCheckInTime())
                .checkOutTime(record.getCheckOutTime())
                .isLate(record.isLate())
                .lateMinutes(record.getLateMinutes())
                .isEarlyLeave(record.isEarlyLeave())
                .earlyLeaveMinutes(record.getEarlyLeaveMinutes())
                .anomalyType(record.getAnomalyType().name())
                .isCorrected(record.isCorrected())
                .status(record.getAnomalyType().name())
                .build();
    }
}
