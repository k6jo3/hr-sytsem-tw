package com.company.hrms.attendance.infrastructure.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.company.hrms.attendance.infrastructure.po.LeaveApplicationPO;

@Mapper
public interface LeaveApplicationMapper {
    LeaveApplicationPO selectById(@Param("id") String id);

    List<LeaveApplicationPO> selectByEmployeeId(@Param("employeeId") String employeeId);

    List<LeaveApplicationPO> selectByStatus(@Param("status") String status);

    List<LeaveApplicationPO> selectByEmployeeIdAndDateRange(@Param("employeeId") String employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    int insert(LeaveApplicationPO application);

    int update(LeaveApplicationPO application);

    int deleteById(@Param("id") String id);
}
