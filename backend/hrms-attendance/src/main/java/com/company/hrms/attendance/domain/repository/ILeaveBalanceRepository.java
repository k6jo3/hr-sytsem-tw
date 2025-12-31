package com.company.hrms.attendance.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.LeaveBalance;
import com.company.hrms.attendance.domain.model.valueobject.BalanceId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;

public interface ILeaveBalanceRepository {
    void save(LeaveBalance balance);

    Optional<LeaveBalance> findById(BalanceId id);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(String employeeId, LeaveTypeId leaveTypeId, int year);

    List<LeaveBalance> findByEmployeeIdAndYear(String employeeId, int year);

    void delete(BalanceId id);
}
