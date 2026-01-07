package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.valueobject.AnomalyType;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.attendance.infrastructure.dao.AttendanceRecordDAO;
import com.company.hrms.attendance.infrastructure.po.AttendanceRecordPO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class AttendanceRecordRepositoryImpl extends QueryBaseRepository<AttendanceRecord, RecordId>
        implements IAttendanceRecordRepository {

    private final AttendanceRecordDAO attendanceRecordDAO;

    public AttendanceRecordRepositoryImpl(JPAQueryFactory factory, AttendanceRecordDAO attendanceRecordDAO) {
        super(factory, AttendanceRecord.class);
        this.attendanceRecordDAO = attendanceRecordDAO;
    }

    @Override
    public Optional<AttendanceRecord> findById(RecordId id) {
        return attendanceRecordDAO.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<AttendanceRecord> findByEmployeeIdAndDate(String employeeId, LocalDate date) {
        return attendanceRecordDAO.findByEmployeeIdAndDate(employeeId, date).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceRecord> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate,
            LocalDate endDate) {
        return attendanceRecordDAO.findByEmployeeIdAndDateRange(employeeId, startDate, endDate).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(AttendanceRecord record) {
        AttendanceRecordPO po = toPO(record);
        if (attendanceRecordDAO.findById(po.getId()).isPresent()) {
            po.setUpdatedAt(LocalDateTime.now());
            attendanceRecordDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            attendanceRecordDAO.insert(po);
        }
    }

    @Override
    public void delete(RecordId id) {
        attendanceRecordDAO.deleteById(id.getValue());
    }

    private AttendanceRecord toDomain(AttendanceRecordPO po) {
        return AttendanceRecord.reconstitute(
                new RecordId(po.getId()),
                po.getEmployeeId(),
                po.getDate(),
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
        AttendanceRecordPO po = new AttendanceRecordPO();
        po.setId(record.getId().getValue());
        po.setEmployeeId(record.getEmployeeId());
        po.setDate(record.getDate());
        po.setCheckInTime(record.getCheckInTime());
        po.setCheckOutTime(record.getCheckOutTime());

        // Detailed fields
        po.setIsLate(record.isLate());
        po.setLateMinutes(record.getLateMinutes());
        po.setIsEarlyLeave(record.isEarlyLeave());
        po.setEarlyLeaveMinutes(record.getEarlyLeaveMinutes());
        po.setAnomalyType(record.getAnomalyType().name());
        po.setIsCorrected(record.isCorrected());
        po.setStatus(record.getAnomalyType().name()); // Legacy 'status' can map to AnomalyType or composite?

        return po;
    }
}
