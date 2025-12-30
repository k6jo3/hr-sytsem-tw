package com.company.hrms.attendance.domain.repository;

import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;

public interface IAttendanceRecordRepository {
    void save(AttendanceRecord record);

    Optional<AttendanceRecord> findById(RecordId id);
}
