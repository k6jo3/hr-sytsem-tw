package com.company.hrms.attendance.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.company.hrms.attendance.infrastructure.po.OvertimeApplicationPO;

@Mapper
public interface OvertimeApplicationMapper {
    OvertimeApplicationPO selectById(@Param("id") String id);

    List<OvertimeApplicationPO> selectByEmployeeId(@Param("employeeId") String employeeId);

    List<OvertimeApplicationPO> selectByEmployeeIdAndMonth(@Param("employeeId") String employeeId,
            @Param("year") int year,
            @Param("month") int month);

    int insert(OvertimeApplicationPO application);

    int update(OvertimeApplicationPO application);

    int deleteById(@Param("id") String id);
}
