package com.company.hrms.iam.infrastructure.repository;

import com.company.hrms.iam.domain.model.entity.Permission;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import com.company.hrms.iam.domain.repository.IPermissionRepository;
import com.company.hrms.iam.infrastructure.dao.PermissionDAO;
import com.company.hrms.iam.infrastructure.mapper.PermissionMapper;
import com.company.hrms.iam.infrastructure.po.PermissionPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Permission Repository 實作
 * 實作 Domain 層定義的 IPermissionRepository 介面
 */
@Component
public class PermissionRepositoryImpl implements IPermissionRepository {

    private final PermissionDAO permissionDAO;
    private final PermissionMapper permissionMapper;

    @Autowired
    public PermissionRepositoryImpl(PermissionDAO permissionDAO, PermissionMapper permissionMapper) {
        this.permissionDAO = permissionDAO;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public Optional<Permission> findById(PermissionId id) {
        PermissionPO po = permissionDAO.selectById(id.getValue());
        return Optional.ofNullable(po).map(permissionMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByPermissionCode(String permissionCode) {
        PermissionPO po = permissionDAO.selectByPermissionCode(permissionCode);
        return Optional.ofNullable(po).map(permissionMapper::toDomain);
    }

    @Override
    public List<Permission> findByResource(String resource) {
        List<PermissionPO> poList = permissionDAO.selectByResource(resource);
        return poList.stream()
                .map(permissionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> findAll() {
        List<PermissionPO> poList = permissionDAO.selectAll();
        return poList.stream()
                .map(permissionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> findByRoleId(String roleId) {
        List<PermissionPO> poList = permissionDAO.selectByRoleId(roleId);
        return poList.stream()
                .map(permissionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> findByIds(List<PermissionId> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<String> idStrings = ids.stream()
                .map(PermissionId::getValue)
                .collect(Collectors.toList());
        List<PermissionPO> poList = permissionDAO.selectByIds(idStrings);
        return poList.stream()
                .map(permissionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Permission permission) {
        PermissionPO po = permissionMapper.toPO(permission);
        permissionDAO.insert(po);
    }

    @Override
    public void update(Permission permission) {
        PermissionPO po = permissionMapper.toPO(permission);
        permissionDAO.update(po);
    }

    @Override
    public void deleteById(PermissionId id) {
        permissionDAO.deleteById(id.getValue());
    }

    @Override
    public boolean existsByPermissionCode(String permissionCode) {
        return permissionDAO.existsByPermissionCode(permissionCode);
    }
}
