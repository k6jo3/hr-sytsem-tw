package com.company.hrms.attendance.domain.repository;

import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;

public interface ILeaveApplicationRepository {
    void save(LeaveApplication application);

    Optional<LeaveApplication> findById(ApplicationId id);
}
