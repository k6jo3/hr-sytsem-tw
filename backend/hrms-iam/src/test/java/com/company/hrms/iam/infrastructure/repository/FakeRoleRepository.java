package com.company.hrms.iam.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.model.valueobject.RoleStatus;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IRoleRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 */
public class FakeRoleRepository
        extends InMemoryBaseRepository<Role, RoleId>
        implements IRoleRepository {

    public FakeRoleRepository() {
        super(Role.class, Role::getId);
    }

    @Override
    public Optional<Role> findById(RoleId id) {
        return super.findById(id);
    }

    @Override
    public Optional<Role> findByRoleCode(String roleCode) {
        return findOneByField("roleCode", roleCode);
    }

    @Override
    public Optional<Role> findByRoleCodeAndTenantId(String roleCode, String tenantId) {
        return findAll().stream()
                .filter(role -> roleCode.equals(role.getRoleCode()))
                .filter(role -> {
                    if (tenantId == null) return role.getTenantId() == null;
                    return tenantId.equals(role.getTenantId());
                })
                .findFirst();
    }

    @Override
    public List<Role> findByStatus(RoleStatus status) {
        return findByField("status", status);
    }

    @Override
    public List<Role> findByTenantId(String tenantId) {
        return findByField("tenantId", tenantId);
    }

    @Override
    public List<Role> findSystemRoles() {
        return findAll().stream()
                .filter(Role::isSystemRole)
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> findAll() {
        return super.findAll();
    }

    @Override
    public List<Role> findByUserId(String userId) {
        // InMemory 版本無法直接處理 user-role 關聯表
        // 需由測試直接設定或透過額外資料結構管理
        return List.of();
    }

    @Override
    public int countUsersByRole(RoleId id) {
        // InMemory 版本無法直接處理 user-role 關聯表
        return 0;
    }

    @Override
    public void save(Role role) {
        doSave(role);
    }

    @Override
    public void update(Role role) {
        doSave(role);
    }

    @Override
    public void deleteById(RoleId id) {
        super.deleteById(id);
    }

    @Override
    public boolean existsByRoleCode(String roleCode) {
        return existsByField("roleCode", roleCode);
    }

    @Override
    public boolean existsByRoleCodeAndTenantId(String roleCode, String tenantId) {
        return findByRoleCodeAndTenantId(roleCode, tenantId).isPresent();
    }

    @Override
    public Page<Role> findPage(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    @Override
    public List<Role> findAll(QueryGroup query) {
        return super.findAll(query);
    }
}
