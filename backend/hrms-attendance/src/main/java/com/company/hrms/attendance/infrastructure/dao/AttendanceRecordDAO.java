package com.company.hrms.attendance.infrastructure.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.infrastructure.mapper.AttendanceRecordMapper;
import com.company.hrms.attendance.infrastructure.po.AttendanceRecordPO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AttendanceRecordDAO {

    private final AttendanceRecordMapper attendanceRecordMapper;

    public Optional<AttendanceRecordPO> findById(String id) {
        return Optional.ofNullable(attendanceRecordMapper.selectById(id));
    }

    public List<AttendanceRecordPO> findByEmployeeIdAndDate(String employeeId, LocalDate date) {
        return attendanceRecordMapper.selectByEmployeeIdAndDate(employeeId, date);
    }

    public List<AttendanceRecordPO> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate,
            LocalDate endDate) {
        return attendanceRecordMapper.selectByEmployeeIdAndDateRange(employeeId, startDate, endDate);
    }

    public void insert(AttendanceRecordPO record) {
        attendanceRecordMapper.insert(record);
    }

    public void update(AttendanceRecordPO record) {
        attendanceRecordMapper.update(record);
    }

    public void deleteById(String id) {
        attendanceRecordMapper.deleteById(id);
    }
}
