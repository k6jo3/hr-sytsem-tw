package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.DepartmentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 部門 Repository 介面
 */
public interface IDepartmentRepository {

    /**
     * 依 ID 查詢
     * @param id 部門 ID
     * @return 部門
     */
    Optional<Department> findById(DepartmentId id);

    /**
     * 依 ID 查詢
     * @param id 部門 ID
     * @return 部門
     */
    Optional<Department> findById(UUID id);

    /**
     * 依組織 ID 查詢部門
     * @param organizationId 組織 ID
     * @return 部門列表
     */
    List<Department> findByOrganizationId(UUID organizationId);

    /**
     * 依組織 ID 和狀態查詢
     * @param organizationId 組織 ID
     * @param status 狀態
     * @return 部門列表
     */
    List<Department> findByOrganizationIdAndStatus(UUID organizationId, DepartmentStatus status);

    /**
     * 依上級部門 ID 查詢子部門
     * @param parentDepartmentId 上級部門 ID
     * @return 子部門列表
     */
    List<Department> findByParentDepartmentId(UUID parentDepartmentId);

    /**
     * 依主管 ID 查詢部門
     * @param managerId 主管 ID
     * @return 部門列表
     */
    List<Department> findByManagerId(UUID managerId);

    /**
     * 查詢組織的一級部門
     * @param organizationId 組織 ID
     * @return 一級部門列表
     */
    List<Department> findTopLevelByOrganizationId(UUID organizationId);

    /**
     * 儲存部門
     * @param department 部門
     */
    void save(Department department);

    /**
     * 檢查部門代碼是否存在
     * @param departmentCode 部門代碼
     * @param organizationId 組織 ID
     * @return 是否存在
     */
    boolean existsByDepartmentCodeAndOrganizationId(String departmentCode, UUID organizationId);

    /**
     * 計算子部門數
     * @param parentDepartmentId 上級部門 ID
     * @param status 狀態
     * @return 子部門數
     */
    int countByParentDepartmentIdAndStatus(UUID parentDepartmentId, DepartmentStatus status);

    /**
     * 計算部門下的在職員工數
     * @param departmentId 部門 ID
     * @return 員工數
     */
    int countActiveEmployees(UUID departmentId);
}
