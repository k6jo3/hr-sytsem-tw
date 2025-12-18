package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.Email;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.EmploymentStatus;
import com.company.hrms.organization.domain.model.valueobject.NationalId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 員工 Repository 介面
 */
public interface IEmployeeRepository {

    /**
     * 依 ID 查詢
     * @param id 員工 ID
     * @return 員工
     */
    Optional<Employee> findById(EmployeeId id);

    /**
     * 依員工編號查詢
     * @param employeeNumber 員工編號
     * @return 員工
     */
    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    /**
     * 依 Email 查詢
     * @param email Email
     * @return 員工
     */
    Optional<Employee> findByEmail(String email);

    /**
     * 依部門 ID 查詢員工
     * @param departmentId 部門 ID
     * @return 員工列表
     */
    List<Employee> findByDepartmentId(DepartmentId departmentId);

    /**
     * 條件查詢 (分頁)
     * @param criteria 查詢條件
     * @return 員工列表
     */
    List<Employee> findByCriteria(EmployeeQueryCriteria criteria);

    /**
     * 條件查詢筆數
     * @param criteria 查詢條件
     * @return 筆數
     */
    long countByCriteria(EmployeeQueryCriteria criteria);

    /**
     * 儲存員工
     * @param employee 員工
     */
    void save(Employee employee);

    /**
     * 刪除員工
     * @param id 員工 ID
     */
    void delete(EmployeeId id);

    /**
     * 檢查員工 ID 是否存在
     * @param id 員工 ID
     * @return 是否存在
     */
    boolean existsById(EmployeeId id);

    /**
     * 檢查員工編號是否存在
     * @param employeeNumber 員工編號
     * @return 是否存在
     */
    boolean existsByEmployeeNumber(String employeeNumber);

    /**
     * 檢查 Email 是否存在
     * @param email Email
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 檢查身分證號是否存在
     * @param nationalId 身分證號
     * @return 是否存在
     */
    boolean existsByNationalId(String nationalId);

    /**
     * 檢查身分證號是否存在 (使用 Value Object)
     * @param nationalId 身分證號
     * @return 是否存在
     */
    boolean existsByNationalId(NationalId nationalId);

    /**
     * 檢查 Email 是否存在 (使用 Value Object)
     * @param email Email
     * @return 是否存在
     */
    boolean existsByEmail(Email email);

    /**
     * 查詢特定前綴的最大流水號
     * 用於生成員工編號
     * @param prefix 員工編號前綴 (例如: EMP202412-)
     * @return 最大流水號，如果沒有則回傳 0
     */
    int findMaxSequenceByPrefix(String prefix);

    /**
     * 查詢條件類別
     */
    class EmployeeQueryCriteria {
        private String keyword;
        private String departmentId;
        private EmploymentStatus employmentStatus;
        private String employmentType;
        private LocalDate hireDateFrom;
        private LocalDate hireDateTo;
        private int page = 1;
        private int pageSize = 20;

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(String departmentId) {
            this.departmentId = departmentId;
        }

        public EmploymentStatus getEmploymentStatus() {
            return employmentStatus;
        }

        public void setEmploymentStatus(EmploymentStatus employmentStatus) {
            this.employmentStatus = employmentStatus;
        }

        public String getEmploymentType() {
            return employmentType;
        }

        public void setEmploymentType(String employmentType) {
            this.employmentType = employmentType;
        }

        public LocalDate getHireDateFrom() {
            return hireDateFrom;
        }

        public void setHireDateFrom(LocalDate hireDateFrom) {
            this.hireDateFrom = hireDateFrom;
        }

        public LocalDate getHireDateTo() {
            return hireDateTo;
        }

        public void setHireDateTo(LocalDate hireDateTo) {
            this.hireDateTo = hireDateTo;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }
}
