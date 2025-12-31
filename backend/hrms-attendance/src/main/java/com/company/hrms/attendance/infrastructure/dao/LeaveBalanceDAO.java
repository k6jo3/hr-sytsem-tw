package com.company.hrms.attendance.infrastructure.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.infrastructure.mapper.LeaveBalanceMapper;
import com.company.hrms.attendance.infrastructure.po.LeaveBalancePO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LeaveBalanceDAO {

    private final LeaveBalanceMapper leaveBalanceMapper;

    public Optional<LeaveBalancePO> findById(String id) {
        return Optional.ofNullable(leaveBalanceMapper.selectById(id));
    }

    public Optional<LeaveBalancePO> findByEmployeeIdAndLeaveTypeIdAndYear(String employeeId, String leaveTypeId,
            int year) {
        return Optional
                .ofNullable(leaveBalanceMapper.selectByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year));
    }

    public List<LeaveBalancePO> findByEmployeeIdAndYear(String employeeId, int year) {
        return leaveBalanceMapper.selectByEmployeeIdAndYear(employeeId, year);
    }

    public void insert(LeaveBalancePO balance) {
        leaveBalanceMapper.insert(balance);
    }

    public void update(LeaveBalancePO balance) {
        leaveBalanceMapper.update(balance);
    }

    public void deleteById(String id) {
        leaveBalanceMapper.deleteById(id);
    }
}
