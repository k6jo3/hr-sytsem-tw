package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;

import java.util.List;
import java.util.Optional;

/**
 * 部門 Repository 介面
 */
public interface IDepartmentRepository {

    /**
     * 依 ID 查詢
     */
    Optional<Department> findById(DepartmentId id);

    /**
     * 依代碼查詢
     */
    Optional<Department> findByCode(String code);

    /**
     * 依組織 ID 查詢所有部門
     */
    List<Department> findByOrganizationId(OrganizationId organizationId);

    /**
     * 依組織 ID 查詢所有部門 (for domain service)
     */
    default List<Department> findByOrganizationId(String organizationId) {
        return findByOrganizationId(new OrganizationId(organizationId));
    }

    /**
     * 依上級部門 ID 查詢子部門
     */
    List<Department> findByParentId(DepartmentId parentId);

    /**
     * 查詢組織的根部門
     */
    List<Department> findRootDepartments(OrganizationId organizationId);

    /**
     * 儲存部門
     */
    void save(Department department);

    /**
     * 刪除部門
     */
    void delete(DepartmentId id);

    /**
     * 檢查代碼是否存在
     */
    boolean existsByCode(String code);

    /**
     * 檢查 ID 是否存在
     */
    boolean existsById(DepartmentId id);

    /**
     * 計算子部門數
     */
    int countByParentId(DepartmentId parentId);
}
