package com.company.hrms.attendance.domain.repository;

import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;

public interface IOvertimeApplicationRepository {
    void save(OvertimeApplication application);

    Optional<OvertimeApplication> findById(OvertimeId id);
}
