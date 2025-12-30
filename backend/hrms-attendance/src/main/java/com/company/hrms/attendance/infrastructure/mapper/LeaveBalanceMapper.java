package com.company.hrms.attendance.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.company.hrms.attendance.infrastructure.po.LeaveBalancePO;

@Mapper
public interface LeaveBalanceMapper {
    LeaveBalancePO selectById(@Param("id") String id);

    LeaveBalancePO selectByEmployeeIdAndLeaveTypeIdAndYear(@Param("employeeId") String employeeId,
            @Param("leaveTypeId") String leaveTypeId,
            @Param("year") int year);

    List<LeaveBalancePO> selectByEmployeeIdAndYear(@Param("employeeId") String employeeId, @Param("year") int year);

    int insert(LeaveBalancePO balance);

    int update(LeaveBalancePO balance);

    int deleteById(@Param("id") String id);
}
