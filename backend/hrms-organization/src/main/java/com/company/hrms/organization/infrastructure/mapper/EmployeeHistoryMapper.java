package com.company.hrms.organization.infrastructure.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.company.hrms.organization.infrastructure.po.EmployeeHistoryPO;

/**
 * 員工人事歷程 MyBatis Mapper
 */
@Mapper
public interface EmployeeHistoryMapper {

    /**
     * 根據 ID 查詢歷程
     */
    EmployeeHistoryPO selectById(@Param("id") String id);

    /**
     * 根據員工 ID 查詢歷程
     */
    List<EmployeeHistoryPO> selectByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 根據事件類型查詢
     */
    List<EmployeeHistoryPO> selectByEventType(@Param("eventType") String eventType);

    /**
     * 根據日期範圍查詢
     */
    List<EmployeeHistoryPO> selectByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 新增歷程
     */
    int insert(EmployeeHistoryPO history);

    /**
     * 更新歷程
     */
    int update(EmployeeHistoryPO history);

    /**
     * 刪除歷程
     */
    int deleteById(@Param("id") String id);

    /**
     * 根據員工 ID 刪除所有歷程
     */
    int deleteByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 檢查歷程是否存在
     */
    boolean existsById(@Param("id") String id);
}
