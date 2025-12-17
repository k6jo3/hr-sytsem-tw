package com.company.hrms.organization.infrastructure.dao;

import com.company.hrms.organization.infrastructure.mapper.DepartmentMapper;
import com.company.hrms.organization.infrastructure.po.DepartmentPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 部門 DAO
 */
@Repository
@RequiredArgsConstructor
public class DepartmentDAO {

    private final DepartmentMapper departmentMapper;

    public Optional<DepartmentPO> findById(String id) {
        return Optional.ofNullable(departmentMapper.selectById(id));
    }

    public Optional<DepartmentPO> findByCode(String code) {
        return Optional.ofNullable(departmentMapper.selectByCode(code));
    }

    public List<DepartmentPO> findByOrganizationId(String organizationId) {
        return departmentMapper.selectByOrganizationId(organizationId);
    }

    public List<DepartmentPO> findByParentId(String parentId) {
        return departmentMapper.selectByParentId(parentId);
    }

    public List<DepartmentPO> findRootDepartments(String organizationId) {
        return departmentMapper.selectRootDepartments(organizationId);
    }

    public List<DepartmentPO> findByManagerId(String managerId) {
        return departmentMapper.selectByManagerId(managerId);
    }

    public List<DepartmentPO> findByStatus(String status) {
        return departmentMapper.selectByStatus(status);
    }

    public void insert(DepartmentPO department) {
        departmentMapper.insert(department);
    }

    public void update(DepartmentPO department) {
        departmentMapper.update(department);
    }

    public void deleteById(String id) {
        departmentMapper.deleteById(id);
    }

    public boolean existsByCode(String code) {
        return departmentMapper.existsByCode(code);
    }

    public boolean existsById(String id) {
        return departmentMapper.existsById(id);
    }

    public int countByParentId(String parentId) {
        return departmentMapper.countByParentId(parentId);
    }
}
