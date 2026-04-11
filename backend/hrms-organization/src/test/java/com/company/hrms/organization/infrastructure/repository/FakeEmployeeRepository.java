package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.Email;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.EmploymentStatus;
import com.company.hrms.organization.domain.model.valueobject.NationalId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * IEmployeeRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeEmployeeRepository repo = new FakeEmployeeRepository();
 * repo.seed(employee1, employee2);
 *
 * Optional&lt;Employee&gt; found = repo.findByEmployeeNumber("EMP202401-001");
 * assertThat(found).isPresent();
 * </pre>
 */
public class FakeEmployeeRepository
        extends InMemoryBaseRepository<Employee, EmployeeId>
        implements IEmployeeRepository {

    public FakeEmployeeRepository() {
        super(Employee.class, Employee::getId);
    }

    // ==================== 單筆查詢 ====================

    @Override
    public Optional<Employee> findById(EmployeeId id) {
        return super.findById(id);
    }

    @Override
    public Optional<Employee> findByEmployeeNumber(String employeeNumber) {
        return findOneByField("employeeNumber", employeeNumber);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        // Employee 的 email 欄位為 companyEmail（Email 值對象），需用 getValue() 比較
        return findAll().stream()
                .filter(e -> {
                    Email companyEmail = e.getCompanyEmail();
                    return companyEmail != null && companyEmail.getValue().equals(email);
                })
                .findFirst();
    }

    // ==================== 批量查詢 ====================

    @Override
    public List<Employee> findByIdIn(Set<EmployeeId> ids) {
        return super.findByIdIn(ids);
    }

    @Override
    public List<Employee> findByDepartmentId(DepartmentId departmentId) {
        // Employee 的 departmentId 欄位型別為 UUID，需解包 DepartmentId
        return findByField("departmentId", departmentId.getValue());
    }

    @Override
    public List<Employee> findAll() {
        return super.findAll();
    }

    // ==================== 條件查詢 ====================

    @Override
    public List<Employee> findByCriteria(EmployeeQueryCriteria criteria) {
        // 使用 stream 進行多欄位過濾
        return findAll().stream()
                .filter(e -> matchesCriteria(e, criteria))
                .skip((long) (criteria.getPage() - 1) * criteria.getPageSize())
                .limit(criteria.getPageSize())
                .collect(Collectors.toList());
    }

    @Override
    public long countByCriteria(EmployeeQueryCriteria criteria) {
        return findAll().stream()
                .filter(e -> matchesCriteria(e, criteria))
                .count();
    }

    // ==================== 動態查詢（QueryGroup） ====================

    @Override
    public List<Employee> findByQuery(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).getContent();
    }

    @Override
    public long countByQuery(QueryGroup query) {
        return super.count(query);
    }

    // ==================== 存在性檢查 ====================

    @Override
    public boolean existsById(EmployeeId id) {
        return super.existsById(id);
    }

    @Override
    public boolean existsByEmployeeNumber(String employeeNumber) {
        return existsByField("employeeNumber", employeeNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        // nationalId 欄位為 NationalId 值對象，需用 getValue() 比較
        return findAll().stream()
                .anyMatch(e -> {
                    NationalId nid = e.getNationalId();
                    return nid != null && nid.getValue().equals(nationalId);
                });
    }

    @Override
    public boolean existsByNationalId(NationalId nationalId) {
        return nationalId != null && existsByNationalId(nationalId.getValue());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return email != null && existsByEmail(email.getValue());
    }

    // ==================== 刪除 ====================

    @Override
    public void delete(EmployeeId id) {
        super.deleteById(id);
    }

    // ==================== 統計查詢 ====================

    @Override
    public int findMaxSequenceByPrefix(String prefix) {
        // 從所有員工編號中找出符合前綴的最大流水號
        return findAll().stream()
                .map(Employee::getEmployeeNumber)
                .filter(num -> num != null && num.startsWith(prefix))
                .map(num -> {
                    // 取出前綴後的數字部分
                    String suffix = num.substring(prefix.length());
                    try {
                        return Integer.parseInt(suffix);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);
    }

    @Override
    public int countByDepartmentId(DepartmentId departmentId) {
        return (int) findAll().stream()
                .filter(e -> e.getDepartmentId() != null
                        && e.getDepartmentId().equals(departmentId.getValue()))
                .count();
    }

    @Override
    public int countByOrganizationId(OrganizationId organizationId) {
        return (int) findAll().stream()
                .filter(e -> e.getOrganizationId() != null
                        && e.getOrganizationId().equals(organizationId.getValue()))
                .count();
    }

    // ==================== 內部方法 ====================

    /**
     * 判斷員工是否符合查詢條件
     */
    private boolean matchesCriteria(Employee employee, EmployeeQueryCriteria criteria) {
        // 關鍵字搜尋（姓名、員工編號）
        if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
            String keyword = criteria.getKeyword().toLowerCase();
            boolean matchName = employee.getFullName() != null
                    && employee.getFullName().toLowerCase().contains(keyword);
            boolean matchNumber = employee.getEmployeeNumber() != null
                    && employee.getEmployeeNumber().toLowerCase().contains(keyword);
            if (!matchName && !matchNumber) {
                return false;
            }
        }

        // 部門篩選
        if (criteria.getDepartmentId() != null && !criteria.getDepartmentId().isBlank()) {
            if (employee.getDepartmentId() == null
                    || !employee.getDepartmentId().toString().equals(criteria.getDepartmentId())) {
                return false;
            }
        }

        // 在職狀態篩選
        if (criteria.getEmploymentStatus() != null) {
            if (employee.getEmploymentStatus() != criteria.getEmploymentStatus()) {
                return false;
            }
        }

        // 雇用類型篩選
        if (criteria.getEmploymentType() != null && !criteria.getEmploymentType().isBlank()) {
            if (employee.getEmploymentType() == null
                    || !employee.getEmploymentType().name().equals(criteria.getEmploymentType())) {
                return false;
            }
        }

        // 到職日期區間篩選
        if (criteria.getHireDateFrom() != null) {
            if (employee.getHireDate() == null
                    || employee.getHireDate().isBefore(criteria.getHireDateFrom())) {
                return false;
            }
        }
        if (criteria.getHireDateTo() != null) {
            if (employee.getHireDate() == null
                    || employee.getHireDate().isAfter(criteria.getHireDateTo())) {
                return false;
            }
        }

        return true;
    }
@Override    public void save(Employee employee) {        doSave(employee);    }
}
