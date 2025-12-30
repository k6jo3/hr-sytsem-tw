package com.company.hrms.attendance.domain.repository;

import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.LeaveType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;

public interface ILeaveTypeRepository {
    void save(LeaveType leaveType);

    Optional<LeaveType> findById(LeaveTypeId id);
}
