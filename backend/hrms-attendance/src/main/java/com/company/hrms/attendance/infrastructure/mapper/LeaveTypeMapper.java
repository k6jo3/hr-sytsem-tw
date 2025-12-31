package com.company.hrms.attendance.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.company.hrms.attendance.infrastructure.po.LeaveTypePO;

@Mapper
public interface LeaveTypeMapper {
    LeaveTypePO selectById(@Param("id") String id);

    LeaveTypePO selectByCode(@Param("code") String code);

    List<LeaveTypePO> selectAll();

    int insert(LeaveTypePO leaveType);

    int update(LeaveTypePO leaveType);

    int deleteById(@Param("id") String id);
}
