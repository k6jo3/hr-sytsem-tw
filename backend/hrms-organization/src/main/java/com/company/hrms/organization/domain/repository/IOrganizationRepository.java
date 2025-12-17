package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 組織 Repository 介面
 */
public interface IOrganizationRepository {

    /**
     * 依 ID 查詢
     * @param id 組織 ID
     * @return 組織
     */
    Optional<Organization> findById(OrganizationId id);

    /**
     * 依 ID 查詢
     * @param id 組織 ID
     * @return 組織
     */
    Optional<Organization> findById(UUID id);

    /**
     * 依組織代碼查詢
     * @param organizationCode 組織代碼
     * @return 組織
     */
    Optional<Organization> findByOrganizationCode(String organizationCode);

    /**
     * 查詢所有母公司
     * @return 母公司列表
     */
    List<Organization> findAllParentOrganizations();

    /**
     * 依母公司 ID 查詢子公司
     * @param parentOrganizationId 母公司 ID
     * @return 子公司列表
     */
    List<Organization> findByParentOrganizationId(UUID parentOrganizationId);

    /**
     * 依狀態查詢
     * @param status 狀態
     * @return 組織列表
     */
    List<Organization> findByStatus(OrganizationStatus status);

    /**
     * 查詢所有組織
     * @return 組織列表
     */
    List<Organization> findAll();

    /**
     * 儲存組織
     * @param organization 組織
     */
    void save(Organization organization);

    /**
     * 檢查組織代碼是否存在
     * @param organizationCode 組織代碼
     * @return 是否存在
     */
    boolean existsByOrganizationCode(String organizationCode);

    /**
     * 計算組織下的在職員工數
     * @param organizationId 組織 ID
     * @return 員工數
     */
    int countActiveEmployees(UUID organizationId);
}
