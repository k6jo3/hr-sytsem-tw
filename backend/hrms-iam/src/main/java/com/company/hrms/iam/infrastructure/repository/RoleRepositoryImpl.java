package com.company.hrms.iam.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.model.valueobject.RoleStatus;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import com.company.hrms.iam.infrastructure.dao.RoleDAO;
import com.company.hrms.iam.infrastructure.mapper.RoleMapper;
import com.company.hrms.iam.infrastructure.po.RolePO;

/**
 * Role Repository 實作
 * 實作 Domain 層定義的 IRoleRepository 介面
 */
@Component
public class RoleRepositoryImpl implements IRoleRepository {

    private final RoleDAO roleDAO;
    private final RoleMapper roleMapper;

    public RoleRepositoryImpl(RoleDAO roleDAO, RoleMapper roleMapper) {
        this.roleDAO = roleDAO;
        this.roleMapper = roleMapper;
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
}
