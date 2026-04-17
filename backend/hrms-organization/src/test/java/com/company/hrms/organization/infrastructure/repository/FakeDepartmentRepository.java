package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IDepartmentRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeDepartmentRepository repo = new FakeDepartmentRepository();
 * repo.seed(department1, department2);
 *
 * Optional&lt;Department&gt; found = repo.findByCode("IT");
 * assertThat(found).isPresent();
 * </pre>
 */
public class FakeDepartmentRepository
        extends InMemoryBaseRepository<Department, DepartmentId>
        implements IDepartmentRepository {

    public FakeDepartmentRepository() {
        super(Department.class, Department::getId);
    }

    // ==================== 單筆查詢 ====================

    @Override
    public Optional<Department> findById(DepartmentId id) {
        return super.findById(id);
    }

    @Override
    public Optional<Department> findByCode(String code) {
        return findOneByField("code", code);
    }

    // ==================== 多筆查詢 ====================

    @Override
    public List<Department> findByOrganizationId(OrganizationId organizationId) {
        return findByField("organizationId", organizationId);
    }

    @Override
    public List<Department> findByParentId(DepartmentId parentId) {
        return findByField("parentId", parentId);
    }

    @Override
    public List<Department> findRootDepartments(OrganizationId organizationId) {
        // 根部門：organizationId 相符且 parentId 為 null
        return findAll().stream()
                .filter(d -> d.getOrganizationId() != null
                        && d.getOrganizationId().equals(organizationId)
                        && d.getParentId() == null)
                .collect(Collectors.toList());
    }

    // ==================== 動態查詢（QueryGroup） ====================

    @Override
    public List<Department> findByQuery(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).getContent();
    }

    @Override
    public long countByQuery(QueryGroup query) {
        return super.count(query);
    }

    // ==================== 存在性檢查 ====================

    @Override
    public boolean existsByCode(String code) {
        return existsByField("code", code);
    }

    @Override
    public boolean existsById(DepartmentId id) {
        return super.existsById(id);
    }

    // ==================== 刪除 ====================

    @Override
    public void delete(DepartmentId id) {
        super.deleteById(id);
    }

    // ==================== 統計查詢 ====================

    @Override
    public int countByParentId(DepartmentId parentId) {
        return countByField("parentId", parentId);
    }

    @Override
    public int countByOrganizationId(OrganizationId organizationId) {
        return countByField("organizationId", organizationId);
    }

    // save 繼承自 InMemoryBaseRepository（簽章為 void）
@Override    public void save(Department department) {        doSave(department);    }
}
