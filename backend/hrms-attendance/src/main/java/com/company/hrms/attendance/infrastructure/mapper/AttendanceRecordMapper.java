package com.company.hrms.attendance.infrastructure.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.company.hrms.attendance.infrastructure.po.AttendanceRecordPO;

@Mapper
public interface AttendanceRecordMapper {
    AttendanceRecordPO selectById(@Param("id") String id);

    List<AttendanceRecordPO> selectByEmployeeIdAndDate(@Param("employeeId") String employeeId,
            @Param("date") LocalDate date);

    List<AttendanceRecordPO> selectByEmployeeIdAndDateRange(@Param("employeeId") String employeeId,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    int insert(AttendanceRecordPO record);

    int update(AttendanceRecordPO record);

    int deleteById(@Param("id") String id);
}
