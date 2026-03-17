package com.company.hrms.organization.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.Address;
import com.company.hrms.organization.domain.model.valueobject.BankAccount;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.Email;
import com.company.hrms.organization.domain.model.valueobject.EmergencyContact;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.EmploymentStatus;
import com.company.hrms.organization.domain.model.valueobject.EmploymentType;
import com.company.hrms.organization.domain.model.valueobject.Gender;
import com.company.hrms.organization.domain.model.valueobject.MaritalStatus;
import com.company.hrms.organization.domain.model.valueobject.NationalId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.model.valueobject.TerminationType;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.infrastructure.dao.EmployeeDAO;
import com.company.hrms.organization.infrastructure.po.EmployeePO;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 員工倉儲實作
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class EmployeeRepositoryImpl implements IEmployeeRepository {

    private final EmployeeDAO employeeDAO;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Employee> findById(EmployeeId id) {
        return employeeDAO.findById(id.getValue().toString())
                .map(this::toDomain);
    }

    @Override
    public List<Employee> findByIdIn(java.util.Set<EmployeeId> ids) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<String> idStrings = ids.stream()
                .map(id -> id.getValue().toString())
                .collect(Collectors.toList());

        return employeeDAO.findByIds(idStrings).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findAll() {
        return employeeDAO.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Employee> findByEmployeeNumber(String employeeNumber) {
        return employeeDAO.findByEmployeeNumber(employeeNumber)
                .map(this::toDomain);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return employeeDAO.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public List<Employee> findByDepartmentId(DepartmentId departmentId) {
        return employeeDAO.findByDepartmentId(departmentId.getValue().toString()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByCriteria(EmployeeQueryCriteria criteria) {
        int offset = (criteria.getPage() - 1) * criteria.getPageSize();
        return employeeDAO.findByCriteria(
                criteria.getKeyword(),
                criteria.getDepartmentId(),
                criteria.getEmploymentStatus() != null ? criteria.getEmploymentStatus().name() : null,
                criteria.getEmploymentType(),
                criteria.getHireDateFrom(),
                criteria.getHireDateTo(),
                offset,
                criteria.getPageSize()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByCriteria(EmployeeQueryCriteria criteria) {
        return employeeDAO.countByCriteria(
                criteria.getKeyword(),
                criteria.getDepartmentId(),
                criteria.getEmploymentStatus() != null ? criteria.getEmploymentStatus().name() : null,
                criteria.getEmploymentType(),
                criteria.getHireDateFrom(),
                criteria.getHireDateTo());
    }

    @Override
    public void save(Employee employee) {
        EmployeePO po = toPO(employee);
        if (employeeDAO.existsById(employee.getId().getValue().toString())) {
            po.setUpdatedAt(LocalDateTime.now());
            employeeDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            employeeDAO.insert(po);
        }
    }

    @Override
    public void delete(EmployeeId id) {
        employeeDAO.deleteById(id.getValue().toString());
    }

    @Override
    public boolean existsById(EmployeeId id) {
        return employeeDAO.existsById(id.getValue().toString());
    }

    @Override
    public boolean existsByEmployeeNumber(String employeeNumber) {
        return employeeDAO.existsByEmployeeNumber(employeeNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return employeeDAO.existsByEmail(email);
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return employeeDAO.existsByNationalId(nationalId);
    }

    @Override
    public boolean existsByNationalId(NationalId nationalId) {
        return employeeDAO.existsByNationalId(nationalId.getValue());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return employeeDAO.existsByEmail(email.getValue());
    }

    @Override
    public int findMaxSequenceByPrefix(String prefix) {
        return employeeDAO.findMaxSequenceByPrefix(prefix);
    }

    @Override
    public int countByDepartmentId(DepartmentId departmentId) {
        return employeeDAO.countByDepartmentId(departmentId.getValue().toString());
    }

    @Override
    public int countByOrganizationId(OrganizationId organizationId) {
        return employeeDAO.countByOrganizationId(organizationId.getValue().toString());
    }

    @Override
    public List<Employee> findByQuery(QueryGroup query,
            org.springframework.data.domain.Pageable pageable) {
        UltimateQueryEngine<EmployeePO> engine = new UltimateQueryEngine<>(jpaQueryFactory, EmployeePO.class);
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
        UltimateQueryEngine<EmployeePO> engine = new UltimateQueryEngine<>(jpaQueryFactory, EmployeePO.class);
        QueryGroup mappedQuery = mapQueryFields(query);
        com.querydsl.core.types.dsl.BooleanExpression predicate = engine.parse(mappedQuery);

        Long total = jpaQueryFactory.select(engine.getEntityPath().count())
                .from(engine.getEntityPath())
                .where(predicate)
                .fetchOne();
        return total != null ? total : 0L;
    }

    /**
     * 將 QueryGroup 中的欄位名稱轉換為 PO 的屬性名稱 (snake_case -> camelCase)
     */
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
            case "employment_status":
                return "employmentStatus";
            case "is_deleted":
                return "isDeleted";
            case "department_id":
                return "departmentId";
            case "full_name":
                return "fullName";
            case "employee_number":
                return "employeeNumber";
            case "employment_type":
                return "employmentType";
            case "hire_date":
                return "hireDate";
            case "supervisor_id":
                return "supervisorId";
            default:
                return field;
        }
    }

    private Employee toDomain(EmployeePO po) {
        Address address = null;
        if (po.getAddressCity() != null) {
            address = new Address(
                    po.getAddressPostalCode(),
                    po.getAddressCity(),
                    po.getAddressDistrict(),
                    po.getAddressStreet());
        }

        EmergencyContact emergencyContact = null;
        if (po.getEmergencyContactName() != null) {
            emergencyContact = new EmergencyContact(
                    po.getEmergencyContactName(),
                    po.getEmergencyContactRelationship(),
                    po.getEmergencyContactPhone());
        }

        BankAccount bankAccount = null;
        if (po.getBankCode() != null) {
            bankAccount = new BankAccount(
                    po.getBankCode(),
                    null, // bankName - PO 中無此欄位
                    po.getBankBranchCode(),
                    po.getBankAccountNumber(),
                    po.getBankAccountHolderName());
        }

        return Employee.reconstitute(
                new EmployeeId(po.getId()),
                po.getEmployeeNumber(),
                po.getFirstName(),
                po.getLastName(),
                po.getEnglishName(),
                po.getGender() != null ? Gender.valueOf(po.getGender()) : null,
                po.getBirthDate(),
                NationalId.reconstitute(po.getNationalId()),
                Email.reconstitute(po.getEmail()),
                po.getPhone(),
                po.getMaritalStatus() != null ? MaritalStatus.valueOf(po.getMaritalStatus()) : null,
                address,
                emergencyContact,
                bankAccount,
                po.getOrganizationId() != null
                        ? new com.company.hrms.organization.domain.model.valueobject.OrganizationId(
                                po.getOrganizationId())
                        : null,
                po.getDepartmentId() != null ? new DepartmentId(po.getDepartmentId()) : null,
                po.getJobTitle(),
                po.getJobLevel(),
                po.getEmploymentType() != null ? EmploymentType.valueOf(po.getEmploymentType()) : null,
                po.getEmploymentStatus() != null ? EmploymentStatus.valueOf(po.getEmploymentStatus()) : null,
                po.getHireDate(),
                po.getProbationEndDate(),
                po.getTerminationDate(),
                po.getTerminationReason(),
                po.getTerminationType() != null ? TerminationType.valueOf(po.getTerminationType()) : null,
                po.getSupervisorId() != null ? new EmployeeId(po.getSupervisorId()) : null);
    }

    private EmployeePO toPO(Employee employee) {
        EmployeePO po = new EmployeePO();
        po.setId(employee.getId().getValue().toString());
        po.setEmployeeNumber(employee.getEmployeeNumber());
        po.setFirstName(employee.getFirstName());
        po.setLastName(employee.getLastName());
        po.setEnglishName(employee.getEnglishName());
        po.setGender(employee.getGender() != null ? employee.getGender().name() : null);
        po.setBirthDate(employee.getBirthDate());
        po.setNationalId(employee.getNationalId() != null ? employee.getNationalId().getValue() : null);
        po.setEmail(employee.getEmail() != null ? employee.getEmail().getValue() : null);
        po.setPhone(employee.getPhone());
        po.setMaritalStatus(employee.getMaritalStatus() != null ? employee.getMaritalStatus().name() : null);

        if (employee.getAddress() != null) {
            po.setAddressPostalCode(employee.getAddress().getPostalCode());
            po.setAddressCity(employee.getAddress().getCity());
            po.setAddressDistrict(employee.getAddress().getDistrict());
            po.setAddressStreet(employee.getAddress().getStreet());
        }

        if (employee.getEmergencyContact() != null) {
            po.setEmergencyContactName(employee.getEmergencyContact().getName());
            po.setEmergencyContactRelationship(employee.getEmergencyContact().getRelationship());
            po.setEmergencyContactPhone(employee.getEmergencyContact().getPhone());
        }

        if (employee.getBankAccount() != null) {
            po.setBankCode(employee.getBankAccount().getBankCode());
            po.setBankBranchCode(employee.getBankAccount().getBranchCode());
            po.setBankAccountNumber(employee.getBankAccount().getAccountNumber());
            po.setBankAccountHolderName(employee.getBankAccount().getAccountHolderName());
        }

        po.setDepartmentId(
                employee.getDepartmentIdVO() != null ? employee.getDepartmentIdVO().getValue().toString() : null);
        po.setOrganizationId(
                employee.getOrganizationId() != null ? employee.getOrganizationId().toString() : null);
        po.setJobTitle(employee.getJobTitle());
        po.setJobLevel(employee.getJobLevel());
        po.setEmploymentType(employee.getEmploymentType() != null ? employee.getEmploymentType().name() : null);
        po.setEmploymentStatus(employee.getEmploymentStatus() != null ? employee.getEmploymentStatus().name() : null);
        po.setFullName(employee.getFullName());
        po.setHireDate(employee.getHireDate());
        po.setProbationEndDate(employee.getProbationEndDate());
        po.setTerminationDate(employee.getTerminationDate());
        po.setTerminationReason(employee.getTerminationReason());
        po.setTerminationType(employee.getTerminationType() != null ? employee.getTerminationType().name() : null);
        po.setSupervisorId(
                employee.getSupervisorId() != null ? employee.getSupervisorId().getValue().toString() : null);

        return po;
    }
}
