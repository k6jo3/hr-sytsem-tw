package com.company.hrms.organization.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.DepartmentStatus;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.infrastructure.dao.DepartmentDAO;
import com.company.hrms.organization.infrastructure.po.DepartmentPO;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements IDepartmentRepository {

    private final DepartmentDAO departmentDAO;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Department> findByQuery(QueryGroup query, org.springframework.data.domain.Pageable pageable) {
        UltimateQueryEngine<DepartmentPO> engine = new UltimateQueryEngine<>(jpaQueryFactory, DepartmentPO.class);
        QueryGroup mappedQuery = mapQueryFields(query);
        com.querydsl.core.types.dsl.BooleanExpression predicate = engine.parse(mappedQuery);

        return engine.getQuery()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByQuery(QueryGroup query) {
        UltimateQueryEngine<DepartmentPO> engine = new UltimateQueryEngine<>(jpaQueryFactory, DepartmentPO.class);
        QueryGroup mappedQuery = mapQueryFields(query);
        com.querydsl.core.types.dsl.BooleanExpression predicate = engine.parse(mappedQuery);

        Long total = jpaQueryFactory.select(engine.getEntityPath().count())
                .from(engine.getEntityPath())
                .where(predicate)
                .fetchOne();
        return total != null ? total : 0L;
    }

    private QueryGroup mapQueryFields(QueryGroup original) {
        if (original == null) {
            return null;
        }
        QueryGroup mapped = new QueryGroup(original.getJunction());
        for (FilterUnit unit : original.getConditions()) {
            mapped.add(new FilterUnit(translateFieldName(unit.getField()), unit.getOp(), unit.getValue()));
        }
        for (QueryGroup sub : original.getSubGroups()) {
            mapped.addSubGroup(mapQueryFields(sub));
        }
        return mapped;
    }

    private String translateFieldName(String field) {
        switch (field) {
            case "departmentCode":
                return "code";
            case "departmentName":
                return "name";
            case "organizationId":
                return "organizationId";
            case "parentId":
            case "parent_department_id":
                return "parentId";
            case "status":
                return "status";
            case "is_deleted":
                return "isDeleted";
            default:
                return field;
        }
    }

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
        return departmentDAO.findByOrganizationId(organizationId.getValue().toString())
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
        return departmentDAO.findRootDepartments(organizationId.getValue().toString())
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

    @Override
    public int countByOrganizationId(OrganizationId organizationId) {
        return departmentDAO.countByOrganizationId(organizationId.getValue().toString());
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
                po.getDescription());
    }

    private DepartmentPO toPO(Department entity) {
        DepartmentPO po = new DepartmentPO();
        po.setId(entity.getId().getValue().toString());
        po.setCode(entity.getCode());
        po.setName(entity.getName());
        po.setNameEn(entity.getNameEn());

        if (entity.getOrganizationId() != null) {
            po.setOrganizationId(entity.getOrganizationId().getValue().toString());
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
