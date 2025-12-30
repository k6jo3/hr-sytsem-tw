package com.company.hrms.attendance.domain.repository;

import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.LeaveBalance;
import com.company.hrms.attendance.domain.model.valueobject.BalanceId;

public interface ILeaveBalanceRepository {
    void save(LeaveBalance balance);

    Optional<LeaveBalance> findById(BalanceId id);
}
