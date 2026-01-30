package com.company.hrms.organization.infrastructure.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.organization.infrastructure.mapper.EmployeeMapper;
import com.company.hrms.organization.infrastructure.po.EmployeePO;

import lombok.RequiredArgsConstructor;

/**
 * 員工 DAO
 */
@Repository
@RequiredArgsConstructor
public class EmployeeDAO {

    private final EmployeeMapper employeeMapper;

    public Optional<EmployeePO> findById(String id) {
        return Optional.ofNullable(employeeMapper.selectById(id));
    }

    public Optional<EmployeePO> findByEmployeeNumber(String employeeNumber) {
        return Optional.ofNullable(employeeMapper.selectByEmployeeNumber(employeeNumber));
    }

    public Optional<EmployeePO> findByEmail(String email) {
        return Optional.ofNullable(employeeMapper.selectByEmail(email));
    }

    public Optional<EmployeePO> findByNationalId(String nationalId) {
        return Optional.ofNullable(employeeMapper.selectByNationalId(nationalId));
    }

    public List<EmployeePO> findByDepartmentId(String departmentId) {
        return employeeMapper.selectByDepartmentId(departmentId);
    }

    public List<EmployeePO> findBySupervisorId(String supervisorId) {
        return employeeMapper.selectBySupervisorId(supervisorId);
    }

    public List<EmployeePO> findByEmploymentStatus(String employmentStatus) {
        return employeeMapper.selectByEmploymentStatus(employmentStatus);
    }

    public List<EmployeePO> findByCriteria(String keyword, String departmentId,
            String employmentStatus, String employmentType,
            LocalDate hireDateFrom, LocalDate hireDateTo,
            int offset, int limit) {
        return employeeMapper.selectByCriteria(keyword, departmentId, employmentStatus,
                employmentType, hireDateFrom, hireDateTo, offset, limit);
    }

    public long countByCriteria(String keyword, String departmentId,
            String employmentStatus, String employmentType,
            LocalDate hireDateFrom, LocalDate hireDateTo) {
        return employeeMapper.countByCriteria(keyword, departmentId, employmentStatus,
                employmentType, hireDateFrom, hireDateTo);
    }

    public void insert(EmployeePO employee) {
        employeeMapper.insert(employee);
    }

    public void update(EmployeePO employee) {
        employeeMapper.update(employee);
    }

    public void deleteById(String id) {
        employeeMapper.deleteById(id);
    }

    public boolean existsByEmployeeNumber(String employeeNumber) {
        return employeeMapper.existsByEmployeeNumber(employeeNumber);
    }

    public boolean existsByEmail(String email) {
        return employeeMapper.existsByEmail(email);
    }

    public boolean existsByNationalId(String nationalId) {
        return employeeMapper.existsByNationalId(nationalId);
    }

    public boolean existsById(String id) {
        return employeeMapper.existsById(id);
    }

    public int countByDepartmentId(String departmentId) {
        return employeeMapper.countByDepartmentId(departmentId);
    }

    /**
     * 查詢特定前綴的最大流水號
     * 
     * @param prefix 員工編號前綴
     * @return 最大流水號
     */
    public int findMaxSequenceByPrefix(String prefix) {
        Integer maxSeq = employeeMapper.findMaxSequenceByPrefix(prefix);
        return maxSeq != null ? maxSeq : 0;
    }

    public int countByOrganizationId(String organizationId) {
        return employeeMapper.countByOrganizationId(organizationId);
    }

    public List<EmployeePO> findAll() {
        return employeeMapper.selectAll();
    }
}
