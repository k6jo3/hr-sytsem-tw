package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.infrastructure.dao.DepartmentDAO;
import com.company.hrms.organization.infrastructure.po.DepartmentPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 部門倉儲實作
 */
@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements IDepartmentRepository {

    private final DepartmentDAO departmentDAO;

    @Override
    public Optional<Department> findById(DepartmentId id) {
        return departmentDAO.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public Optional<Department> findByCode(String code) {
        return departmentDAO.findByCode(code)
                .map(this::toDomain);
    }

    @Override
    public List<Department> findByOrganizationId(OrganizationId organizationId) {
        return departmentDAO.findByOrganizationId(organizationId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Department> findByParentId(DepartmentId parentId) {
        return departmentDAO.findByParentId(parentId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Department> findRootDepartments(OrganizationId organizationId) {
        return departmentDAO.findRootDepartments(organizationId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Department department) {
        DepartmentPO po = toPO(department);
        if (departmentDAO.existsById(department.getId().getValue())) {
            po.setUpdatedAt(LocalDateTime.now());
            departmentDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            departmentDAO.insert(po);
        }
    }

    @Override
    public void delete(DepartmentId id) {
        departmentDAO.deleteById(id.getValue());
    }

    @Override
    public boolean existsByCode(String code) {
        return departmentDAO.existsByCode(code);
    }

    @Override
    public boolean existsById(DepartmentId id) {
        return departmentDAO.existsById(id.getValue());
    }

    @Override
    public int countByParentId(DepartmentId parentId) {
        return departmentDAO.countByParentId(parentId.getValue());
    }

    private Department toDomain(DepartmentPO po) {
        return Department.reconstitute(
                new DepartmentId(po.getId()),
                po.getCode(),
                po.getName(),
                po.getNameEn(),
                new OrganizationId(po.getOrganizationId()),
                po.getParentId() != null ? new DepartmentId(po.getParentId()) : null,
                po.getLevel(),
                po.getPath(),
                po.getManagerId() != null ? new EmployeeId(po.getManagerId()) : null,
                DepartmentStatus.valueOf(po.getStatus()),
                po.getSortOrder(),
                po.getDescription()
        );
    }

    private DepartmentPO toPO(Department department) {
        DepartmentPO po = new DepartmentPO();
        po.setId(department.getId().getValue());
        po.setCode(department.getCode());
        po.setName(department.getName());
        po.setNameEn(department.getNameEn());
        po.setOrganizationId(department.getOrganizationId().getValue());
        po.setParentId(department.getParentId() != null ? department.getParentId().getValue() : null);
        po.setLevel(department.getLevel());
        po.setPath(department.getPath());
        po.setManagerId(department.getManagerId() != null ? department.getManagerId().getValue() : null);
        po.setStatus(department.getStatus().name());
        po.setSortOrder(department.getSortOrder());
        po.setDescription(department.getDescription());
        return po;
    }
}
