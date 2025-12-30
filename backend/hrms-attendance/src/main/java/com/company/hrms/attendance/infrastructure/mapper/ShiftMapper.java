package com.company.hrms.attendance.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.company.hrms.attendance.infrastructure.po.ShiftPO;

@Mapper
public interface ShiftMapper {
    ShiftPO selectById(@Param("id") String id);

    int insert(ShiftPO shift);

    int update(ShiftPO shift);

    int deleteById(@Param("id") String id);

    List<ShiftPO> selectAll();
}
