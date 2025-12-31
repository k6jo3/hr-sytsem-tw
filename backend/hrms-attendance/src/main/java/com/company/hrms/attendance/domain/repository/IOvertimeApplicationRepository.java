package com.company.hrms.attendance.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;

public interface IOvertimeApplicationRepository {
    void save(OvertimeApplication application);

    Optional<OvertimeApplication> findById(OvertimeId id);

    List<OvertimeApplication> findByEmployeeId(String employeeId);

    List<OvertimeApplication> findByEmployeeIdAndMonth(String employeeId, int year, int month);

    void delete(OvertimeId id);
}
