package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.aggregate.EmployeeContract;
import com.company.hrms.organization.domain.model.valueobject.ContractId;
import com.company.hrms.organization.domain.model.valueobject.ContractStatus;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 員工合約 Repository 介面
 */
public interface IEmployeeContractRepository {

    /**
     * 依 ID 查詢
     */
    Optional<EmployeeContract> findById(ContractId id);

    /**
     * 依 ID 查詢 (UUID 版本)
     */
    Optional<EmployeeContract> findById(UUID id);

    /**
     * 依員工 ID 查詢合約
     */
    List<EmployeeContract> findByEmployeeId(EmployeeId employeeId);

    /**
     * 依員工 ID 查詢合約 (UUID 版本)
     */
    List<EmployeeContract> findByEmployeeId(UUID employeeId);

    /**
     * 依員工 ID 和狀態查詢
     */
    List<EmployeeContract> findByEmployeeIdAndStatus(UUID employeeId, ContractStatus status);

    /**
     * 查詢員工目前生效的合約
     */
    Optional<EmployeeContract> findActiveByEmployeeId(EmployeeId employeeId);

    /**
     * 查詢員工目前生效的合約 (UUID 版本)
     */
    Optional<EmployeeContract> findActiveByEmployeeId(UUID employeeId);

    /**
     * 查詢即將到期的合約 (指定日期範圍)
     */
    List<EmployeeContract> findExpiringContracts(LocalDate startDate, LocalDate endDate);

    /**
     * 查詢即將到期的合約 (依狀態)
     */
    List<EmployeeContract> findExpiringContracts(LocalDate endDateBefore, ContractStatus status);

    /**
     * 查詢在指定日期前到期的合約
     */
    List<EmployeeContract> findExpiringBefore(LocalDate expiryDate);

    /**
     * 查詢所有即將到期的合約 (30天內)
     */
    List<EmployeeContract> findContractsExpiringSoon();

    /**
     * 儲存合約
     */
    void save(EmployeeContract contract);

    /**
     * 刪除合約
     */
    void delete(ContractId id);

    /**
     * 檢查合約 ID 是否存在
     */
    boolean existsById(ContractId id);

    /**
     * 檢查合約編號是否存在
     */
    boolean existsByContractNumber(String contractNumber);
}
