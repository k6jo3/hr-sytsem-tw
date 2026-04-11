package com.company.hrms.attendance.infrastructure.repository;

import com.company.hrms.attendance.domain.model.aggregate.LeaveBalance;
import com.company.hrms.attendance.domain.model.valueobject.BalanceId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveBalanceRepository;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ILeaveBalanceRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakeLeaveBalanceRepository
        extends InMemoryBaseRepository<LeaveBalance, BalanceId>
        implements ILeaveBalanceRepository {

    public FakeLeaveBalanceRepository() {
        super(LeaveBalance.class, LeaveBalance::getId);
    }

    @Override
    public Optional<LeaveBalance> findById(BalanceId id) {
        return super.findById(id);
    }

    @Override
    public Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(String employeeId, LeaveTypeId leaveTypeId, int year) {
        return findAll().stream()
                .filter(b -> employeeId.equals(b.getEmployeeId()))
                .filter(b -> leaveTypeId.equals(b.getLeaveTypeId()))
                .filter(b -> b.getYear() == year)
                .findFirst();
    }

    @Override
    public List<LeaveBalance> findByEmployeeIdAndYear(String employeeId, int year) {
        return findAll().stream()
                .filter(b -> employeeId.equals(b.getEmployeeId()))
                .filter(b -> b.getYear() == year)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(BalanceId id) {
        super.deleteById(id);
    }
@Override    public void save(LeaveBalance leaveBalance) {        doSave(leaveBalance);    }
}
