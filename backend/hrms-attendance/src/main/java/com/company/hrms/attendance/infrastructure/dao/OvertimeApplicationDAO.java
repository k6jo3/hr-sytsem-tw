package com.company.hrms.attendance.infrastructure.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.infrastructure.mapper.OvertimeApplicationMapper;
import com.company.hrms.attendance.infrastructure.po.OvertimeApplicationPO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OvertimeApplicationDAO {

    private final OvertimeApplicationMapper overtimeApplicationMapper;

    public Optional<OvertimeApplicationPO> findById(String id) {
        return Optional.ofNullable(overtimeApplicationMapper.selectById(id));
    }

    public List<OvertimeApplicationPO> findByEmployeeId(String employeeId) {
        return overtimeApplicationMapper.selectByEmployeeId(employeeId);
    }

    public List<OvertimeApplicationPO> findByEmployeeIdAndMonth(String employeeId, int year, int month) {
        return overtimeApplicationMapper.selectByEmployeeIdAndMonth(employeeId, year, month);
    }

    public void insert(OvertimeApplicationPO application) {
        overtimeApplicationMapper.insert(application);
    }

    public void update(OvertimeApplicationPO application) {
        overtimeApplicationMapper.update(application);
    }

    public void deleteById(String id) {
        overtimeApplicationMapper.deleteById(id);
    }
}
