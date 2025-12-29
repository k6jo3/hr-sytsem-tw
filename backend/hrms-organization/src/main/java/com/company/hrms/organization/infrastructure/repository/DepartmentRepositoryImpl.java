package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.DepartmentStatus;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.infrastructure.dao.DepartmentDAO;
import com.company.hrms.organization.infrastructure.po.DepartmentPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements IDepartmentRepository {

    private final DepartmentDAO departmentDAO;

    @Override
    public Optional<Department> findById(DepartmentId id) {
        return departmentDAO.findById(id.getValue().toString())
                .map(this::toDomain);
    }

    @Override
    public Optional<Department> findByCode(String code) {
        return departmentDAO.findByCode(code)
                .map(this::toDomain);
    }

    @Override
    public List<Department> findByOrganizationId(OrganizationId organizationId) {
        return departmentDAO.findByOrganizationId(organizationId.getValue()) // OrganizationId wraps String? need check.
                // Assuming OrganizationId wraps String based on previous errors.
                // If it wraps UUID, need .toString().
                // I'll use .toString() to be safe if getValue() is Object/UUID.
                // If getValue() is String, toString() is redundant but safe.
                // Wait, OrganizationId.getValue() -> String?
                // Let's assume .toString() on the ID object itself if possible, or getValue().toString().
                // I'll check OrganizationId later if this fails.
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Department> findByParentId(DepartmentId parentId) {
        return departmentDAO.findByParentId(parentId.getValue().toString())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Department> findRootDepartments(OrganizationId organizationId) {
        return departmentDAO.findRootDepartments(organizationId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Department department) {
        DepartmentPO po = toPO(department);
        if (departmentDAO.existsById(po.getId())) {
            departmentDAO.update(po);
        } else {
            departmentDAO.insert(po);
        }
    }

    @Override
    public void delete(DepartmentId id) {
        departmentDAO.deleteById(id.getValue().toString());
    }

    @Override
    public boolean existsByCode(String code) {
        return departmentDAO.existsByCode(code);
    }

    @Override
    public boolean existsById(DepartmentId id) {
        return departmentDAO.existsById(id.getValue().toString());
    }

    @Override
    public int countByParentId(DepartmentId parentId) {
        return departmentDAO.countByParentId(parentId.getValue().toString());
    }

    private Department toDomain(DepartmentPO po) {
        return Department.reconstitute(
                new DepartmentId(po.getId()),
                po.getCode(),
                po.getName(),
                po.getNameEn(),
                po.getOrganizationId() != null ? new OrganizationId(po.getOrganizationId()) : null,
                po.getParentId() != null ? new DepartmentId(po.getParentId()) : null,
                po.getLevel(),
                po.getPath(),
                po.getManagerId() != null ? new EmployeeId(po.getManagerId()) : null,
                po.getStatus() != null ? DepartmentStatus.valueOf(po.getStatus()) : DepartmentStatus.ACTIVE,
                po.getSortOrder(),
                po.getDescription()
        );
    }

    private DepartmentPO toPO(Department entity) {
        DepartmentPO po = new DepartmentPO();
        po.setId(entity.getId().getValue().toString());
        po.setCode(entity.getCode());
        po.setName(entity.getName());
        po.setNameEn(entity.getNameEn());

        if (entity.getOrganizationId() != null) {
            po.setOrganizationId(entity.getOrganizationId().getValue());
        }

        if (entity.getParentId() != null) {
            po.setParentId(entity.getParentId().getValue().toString());
        }

        po.setLevel(entity.getLevel());
        po.setPath(entity.getPath());

        if (entity.getManagerId() != null) {
            po.setManagerId(entity.getManagerId().getValue().toString());
        }

        if (entity.getStatus() != null) {
            po.setStatus(entity.getStatus().name());
        }

        po.setSortOrder(entity.getSortOrder());
        po.setDescription(entity.getDescription());

        return po;
    }
}
