package com.company.hrms.iam.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.model.valueobject.RoleStatus;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import com.company.hrms.iam.infrastructure.dao.RoleDAO;
import com.company.hrms.iam.infrastructure.mapper.RoleMapper;
import com.company.hrms.iam.infrastructure.po.RolePO;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * Role Repository 實作
 * 實作 Domain 層定義的 IRoleRepository 介面
 */
@Component
public class RoleRepositoryImpl implements IRoleRepository {

    private final RoleDAO roleDAO;
    private final RoleMapper roleMapper;
    private final JPAQueryFactory queryFactory;

    public RoleRepositoryImpl(RoleDAO roleDAO, RoleMapper roleMapper, JPAQueryFactory queryFactory) {
        this.roleDAO = roleDAO;
        this.roleMapper = roleMapper;
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<Role> findById(RoleId id) {
        RolePO po = roleDAO.selectById(id.getValue());
        if (po == null) {
            return Optional.empty();
        }
        List<String> permissionIds = roleDAO.selectPermissionIdsByRoleId(id.getValue());
        return Optional.of(roleMapper.toDomain(po, permissionIds));
    }

    @Override
    public Optional<Role> findByRoleCode(String roleCode) {
        RolePO po = roleDAO.selectByRoleCode(roleCode);
        if (po == null) {
            return Optional.empty();
        }
        List<String> permissionIds = roleDAO.selectPermissionIdsByRoleId(po.getRoleId());
        return Optional.of(roleMapper.toDomain(po, permissionIds));
    }

    @Override
    public Optional<Role> findByRoleCodeAndTenantId(String roleCode, String tenantId) {
        RolePO po = roleDAO.selectByRoleCodeAndTenantId(roleCode, tenantId);
        if (po == null) {
            return Optional.empty();
        }
        List<String> permissionIds = roleDAO.selectPermissionIdsByRoleId(po.getRoleId());
        return Optional.of(roleMapper.toDomain(po, permissionIds));
    }

    @Override
    public List<Role> findByStatus(RoleStatus status) {
        List<RolePO> poList = roleDAO.selectByStatus(status.name());
        return poList.stream()
                .map(po -> {
                    List<String> permissionIds = roleDAO.selectPermissionIdsByRoleId(po.getRoleId());
                    return roleMapper.toDomain(po, permissionIds);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> findByTenantId(String tenantId) {
        List<RolePO> poList = roleDAO.selectByTenantId(tenantId);
        return poList.stream()
                .map(po -> {
                    List<String> permissionIds = roleDAO.selectPermissionIdsByRoleId(po.getRoleId());
                    return roleMapper.toDomain(po, permissionIds);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> findSystemRoles() {
        List<RolePO> poList = roleDAO.selectSystemRoles();
        return poList.stream()
                .map(po -> {
                    List<String> permissionIds = roleDAO.selectPermissionIdsByRoleId(po.getRoleId());
                    return roleMapper.toDomain(po, permissionIds);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> findAll() {
        List<RolePO> poList = roleDAO.selectAll();
        return poList.stream()
                .map(po -> {
                    List<String> permissionIds = roleDAO.selectPermissionIdsByRoleId(po.getRoleId());
                    return roleMapper.toDomain(po, permissionIds);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> findByUserId(String userId) {
        List<RolePO> poList = roleDAO.selectByUserId(userId);
        return poList.stream()
                .map(po -> {
                    List<String> permissionIds = roleDAO.selectPermissionIdsByRoleId(po.getRoleId());
                    return roleMapper.toDomain(po, permissionIds);
                })
                .collect(Collectors.toList());
    }

    @Override
    public int countUsersByRole(RoleId id) {
        return roleDAO.countUsersByRoleId(id.getValue());
    }

    @Override
    public void save(Role role) {
        RolePO po = roleMapper.toPO(role);
        roleDAO.insert(po);

        // 儲存權限關聯
        for (PermissionId permissionId : role.getPermissionIds()) {
            roleDAO.insertRolePermission(role.getId().getValue(), permissionId.getValue());
        }
    }

    @Override
    public void update(Role role) {
        RolePO po = roleMapper.toPO(role);
        roleDAO.update(po);

        // 更新權限關聯 (先刪後增)
        roleDAO.deleteRolePermissions(role.getId().getValue());
        for (PermissionId permissionId : role.getPermissionIds()) {
            roleDAO.insertRolePermission(role.getId().getValue(), permissionId.getValue());
        }
    }

    @Override
    public void deleteById(RoleId id) {
        roleDAO.deleteRolePermissions(id.getValue());
        roleDAO.deleteById(id.getValue());
    }

    @Override
    public boolean existsByRoleCode(String roleCode) {
        return roleDAO.existsByRoleCode(roleCode);
    }

    @Override
    public boolean existsByRoleCodeAndTenantId(String roleCode, String tenantId) {
        return roleDAO.existsByRoleCodeAndTenantId(roleCode, tenantId);
    }

    @Override
    public Page<Role> findPage(QueryGroup query, Pageable pageable) {
        UltimateQueryEngine<RolePO> engine = new UltimateQueryEngine<>(queryFactory, RolePO.class);
        com.querydsl.core.types.dsl.BooleanExpression predicate = engine.parse(query);

        Long total = queryFactory.select(engine.getEntityPath().count())
                .from(engine.getEntityPath())
                .where(predicate)
                .fetchOne();
        long totalCount = total != null ? total : 0L;

        if (totalCount == 0) {
            return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
        }

        List<RolePO> poList = engine.getQuery()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Role> roles = poList.stream()
                .map(po -> {
                    List<String> permissionIds = roleDAO.selectPermissionIdsByRoleId(po.getRoleId());
                    return roleMapper.toDomain(po, permissionIds);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(roles, pageable, totalCount);
    }

    @Override
    public List<Role> findAll(QueryGroup query) {
        UltimateQueryEngine<RolePO> engine = new UltimateQueryEngine<>(queryFactory, RolePO.class);
        com.querydsl.core.types.dsl.BooleanExpression predicate = engine.parse(query);
        List<RolePO> poList = engine.getQuery().where(predicate).fetch();
        return poList.stream()
                .map(po -> {
                    List<String> permissionIds = roleDAO.selectPermissionIdsByRoleId(po.getRoleId());
                    return roleMapper.toDomain(po, permissionIds);
                })
                .collect(Collectors.toList());
    }
}
