package com.company.hrms.organization.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

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
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.infrastructure.dao.EmployeeDAO;
import com.company.hrms.organization.infrastructure.po.EmployeePO;

import lombok.RequiredArgsConstructor;

/**
 * 員工倉儲實作
 */
@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements IEmployeeRepository {

    private final EmployeeDAO employeeDAO;

    @Override
    public Optional<Employee> findById(EmployeeId id) {
        return employeeDAO.findById(id.getValue().toString())
                .map(this::toDomain);
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
    public int countByOrganizationId(OrganizationId organizationId) {
        return employeeDAO.countByOrganizationId(organizationId.getValue().toString());
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
                po.getNationalId() != null ? new NationalId(po.getNationalId()) : null,
                po.getEmail() != null ? new Email(po.getEmail()) : null,
                po.getPhone(),
                po.getMaritalStatus() != null ? MaritalStatus.valueOf(po.getMaritalStatus()) : null,
                address,
                emergencyContact,
                bankAccount,
                po.getDepartmentId() != null ? new DepartmentId(po.getDepartmentId()) : null,
                po.getJobTitle(),
                po.getJobLevel(),
                po.getEmploymentType() != null ? EmploymentType.valueOf(po.getEmploymentType()) : null,
                po.getEmploymentStatus() != null ? EmploymentStatus.valueOf(po.getEmploymentStatus()) : null,
                po.getHireDate(),
                po.getProbationEndDate(),
                po.getTerminationDate(),
                po.getTerminationReason(),
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
        po.setJobTitle(employee.getJobTitle());
        po.setJobLevel(employee.getJobLevel());
        po.setEmploymentType(employee.getEmploymentType() != null ? employee.getEmploymentType().name() : null);
        po.setEmploymentStatus(employee.getEmploymentStatus() != null ? employee.getEmploymentStatus().name() : null);
        po.setHireDate(employee.getHireDate());
        po.setProbationEndDate(employee.getProbationEndDate());
        po.setTerminationDate(employee.getTerminationDate());
        po.setTerminationReason(employee.getTerminationReason());
        po.setSupervisorId(
                employee.getSupervisorId() != null ? employee.getSupervisorId().getValue().toString() : null);

        return po;
    }
}
