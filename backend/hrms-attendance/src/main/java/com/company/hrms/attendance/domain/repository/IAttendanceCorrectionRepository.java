package com.company.hrms.attendance.domain.repository;

import java.util.Optional;

import com.company.hrms.attendance.domain.model.entity.AttendanceCorrection;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionId;

public interface IAttendanceCorrectionRepository {
    void save(AttendanceCorrection correction);

    Optional<AttendanceCorrection> findById(CorrectionId id);
}
