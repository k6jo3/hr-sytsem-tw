package com.company.hrms.attendance.infrastructure.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.infrastructure.mapper.LeaveApplicationMapper;
import com.company.hrms.attendance.infrastructure.po.LeaveApplicationPO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LeaveApplicationDAO {

    private final LeaveApplicationMapper leaveApplicationMapper;

    public Optional<LeaveApplicationPO> findById(String id) {
        return Optional.ofNullable(leaveApplicationMapper.selectById(id));
    }

    public List<LeaveApplicationPO> findByEmployeeId(String employeeId) {
        return leaveApplicationMapper.selectByEmployeeId(employeeId);
    }

    public List<LeaveApplicationPO> findByStatus(String status) {
        return leaveApplicationMapper.selectByStatus(status);
    }

    public List<LeaveApplicationPO> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate,
            LocalDate endDate) {
        return leaveApplicationMapper.selectByEmployeeIdAndDateRange(employeeId, startDate, endDate);
    }

    public List<LeaveApplicationPO> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return leaveApplicationMapper.selectByDateRange(startDate, endDate);
    }

    public void insert(LeaveApplicationPO application) {
        leaveApplicationMapper.insert(application);
    }

    public void update(LeaveApplicationPO application) {
        leaveApplicationMapper.update(application);
    }

    public void deleteById(String id) {
        leaveApplicationMapper.deleteById(id);
    }
}
