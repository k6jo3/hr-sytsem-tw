package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

import java.util.List;
import java.util.Optional;

/**
 * IOrganizationRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeOrganizationRepository repo = new FakeOrganizationRepository();
 * repo.seed(Organization.create("ORG001", "測試公司", "Test Corp", "12345678"));
 *
 * Optional&lt;Organization&gt; found = repo.findByCode("ORG001");
 * assertThat(found).isPresent();
 * </pre>
 */
public class FakeOrganizationRepository
        extends InMemoryBaseRepository<Organization, OrganizationId>
        implements IOrganizationRepository {

    public FakeOrganizationRepository() {
        super(Organization.class, Organization::getId);
    }

    // ==================== 單筆查詢 ====================

    @Override
    public Optional<Organization> findById(OrganizationId id) {
        return super.findById(id);
    }

    @Override
    public Optional<Organization> findByCode(String code) {
        return findOneByField("code", code);
    }

    // ==================== 多筆查詢 ====================

    @Override
    public List<Organization> findAll() {
        return super.findAll();
    }

    @Override
    public List<Organization> findByParentId(OrganizationId parentId) {
        return findByField("parentId", parentId);
    }

    // ==================== 存在性檢查 ====================

    @Override
    public boolean existsByCode(String code) {
        return existsByField("code", code);
    }

    @Override
    public boolean existsById(OrganizationId id) {
        return super.existsById(id);
    }

    // ==================== 刪除 ====================

    @Override
    public void delete(OrganizationId id) {
        super.deleteById(id);
    }

    // save 繼承自 InMemoryBaseRepository（簽章為 void）
@Override    public void save(Organization organization) {        doSave(organization);    }
}
