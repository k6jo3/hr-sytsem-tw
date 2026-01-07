package com.company.hrms.attendance.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.IQueryRepository;

public interface IAttendanceRecordRepository extends IQueryRepository<AttendanceRecord, RecordId> {
    void save(AttendanceRecord record);

    Optional<AttendanceRecord> findById(RecordId id);

    List<AttendanceRecord> findByEmployeeIdAndDate(String employeeId, LocalDate date);

    List<AttendanceRecord> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate);

    void delete(RecordId id);
}
