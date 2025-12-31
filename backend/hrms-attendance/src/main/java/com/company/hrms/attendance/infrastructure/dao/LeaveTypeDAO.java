package com.company.hrms.attendance.infrastructure.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.infrastructure.mapper.LeaveTypeMapper;
import com.company.hrms.attendance.infrastructure.po.LeaveTypePO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LeaveTypeDAO {

    private final LeaveTypeMapper leaveTypeMapper;

    public Optional<LeaveTypePO> findById(String id) {
        return Optional.ofNullable(leaveTypeMapper.selectById(id));
    }

    public Optional<LeaveTypePO> findByCode(String code) {
        return Optional.ofNullable(leaveTypeMapper.selectByCode(code));
    }

    public List<LeaveTypePO> findAll() {
        return leaveTypeMapper.selectAll();
    }

    public void insert(LeaveTypePO leaveType) {
        leaveTypeMapper.insert(leaveType);
    }

    public void update(LeaveTypePO leaveType) {
        leaveTypeMapper.update(leaveType);
    }

    public void deleteById(String id) {
        leaveTypeMapper.deleteById(id);
    }
}
