package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;

import java.util.List;
import java.util.Optional;

/**
 * 組織 Repository 介面
 */
public interface IOrganizationRepository {

    /**
     * 依 ID 查詢
     */
    Optional<Organization> findById(OrganizationId id);

    /**
     * 依組織代碼查詢
     */
    Optional<Organization> findByCode(String code);

    /**
     * 查詢所有組織
     */
    List<Organization> findAll();

    /**
     * 依母公司 ID 查詢子公司
     */
    List<Organization> findByParentId(OrganizationId parentId);

    /**
     * 儲存組織
     */
    void save(Organization organization);

    /**
     * 刪除組織
     */
    void delete(OrganizationId id);

    /**
     * 檢查組織代碼是否存在
     */
    boolean existsByCode(String code);

    /**
     * 檢查組織 ID 是否存在
     */
    boolean existsById(OrganizationId id);
}
