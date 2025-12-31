package com.company.hrms.attendance.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.LeaveType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;

public interface ILeaveTypeRepository {
    void save(LeaveType leaveType);

    Optional<LeaveType> findById(LeaveTypeId id);

    Optional<LeaveType> findByCode(String code);

    List<LeaveType> findAll();

    void delete(LeaveTypeId id);
}
