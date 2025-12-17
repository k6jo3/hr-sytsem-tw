package com.company.hrms.organization.infrastructure.mapper;

import com.company.hrms.organization.infrastructure.po.EmployeeContractPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 員工合約 MyBatis Mapper
 */
@Mapper
public interface EmployeeContractMapper {

    /**
     * 根據 ID 查詢合約
     */
    EmployeeContractPO selectById(@Param("id") String id);

    /**
     * 根據員工 ID 查詢合約
     */
    List<EmployeeContractPO> selectByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 查詢員工目前有效合約
     */
    EmployeeContractPO selectActiveByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 查詢即將到期的合約
     */
    List<EmployeeContractPO> selectExpiringContracts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 根據狀態查詢合約
     */
    List<EmployeeContractPO> selectByStatus(@Param("status") String status);

    /**
     * 新增合約
     */
    int insert(EmployeeContractPO contract);

    /**
     * 更新合約
     */
    int update(EmployeeContractPO contract);

    /**
     * 刪除合約
     */
    int deleteById(@Param("id") String id);

    /**
     * 檢查合約 ID 是否存在
     */
    boolean existsById(@Param("id") String id);

    /**
     * 根據員工 ID 和狀態查詢合約
     */
    List<EmployeeContractPO> selectByEmployeeIdAndStatus(
            @Param("employeeId") String employeeId,
            @Param("status") String status
    );

    /**
     * 檢查合約編號是否存在
     */
    boolean existsByContractNumber(@Param("contractNumber") String contractNumber);
}
