package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.EmploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
     * 依 ID 查詢
     * @param id 員工 ID
     * @return 員工
     */
    Optional<Employee> findById(UUID id);

    /**
     * 依員工編號查詢
     * @param employeeNumber 員工編號
     * @return 員工
     */
    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    /**
     * 依公司 Email 查詢
     * @param email 公司 Email
     * @return 員工
     */
    Optional<Employee> findByCompanyEmail(String email);

    /**
     * 依部門 ID 查詢員工
     * @param departmentId 部門 ID
     * @return 員工列表
     */
    List<Employee> findByDepartmentId(UUID departmentId);

    /**
     * 依主管 ID 查詢直屬員工
     * @param managerId 主管 ID
     * @return 員工列表
     */
    List<Employee> findByManagerId(UUID managerId);

    /**
     * 依在職狀態查詢
     * @param status 在職狀態
     * @return 員工列表
     */
    List<Employee> findByStatus(EmploymentStatus status);

    /**
     * 分頁查詢 (支援多條件篩選)
     * @param criteria 查詢條件
     * @param pageable 分頁參數
     * @return 分頁結果
     */
    Page<Employee> findAll(EmployeeQueryCriteria criteria, Pageable pageable);

    /**
     * 儲存員工
     * @param employee 員工
     */
    void save(Employee employee);

    /**
     * 檢查員工編號是否存在
     * @param employeeNumber 員工編號
     * @return 是否存在
     */
    boolean existsByEmployeeNumber(String employeeNumber);

    /**
     * 檢查身分證號是否存在
     * @param nationalId 身分證號 (加密後)
     * @return 是否存在
     */
    boolean existsByNationalId(String nationalId);

    /**
     * 檢查公司 Email 是否存在
     * @param email 公司 Email
     * @return 是否存在
     */
    boolean existsByCompanyEmail(String email);

    /**
     * 計算部門和狀態的員工數
     * @param departmentId 部門 ID
     * @param status 狀態
     * @return 員工數
     */
    int countByDepartmentIdAndStatus(UUID departmentId, EmploymentStatus status);

    /**
     * 查詢條件類別
     */
    class EmployeeQueryCriteria {
        private String search;
        private EmploymentStatus status;
        private UUID departmentId;
        private UUID organizationId;
        private String hireDateFrom;
        private String hireDateTo;

        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
            this.search = search;
        }

        public EmploymentStatus getStatus() {
            return status;
        }

        public void setStatus(EmploymentStatus status) {
            this.status = status;
        }

        public UUID getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(UUID departmentId) {
            this.departmentId = departmentId;
        }

        public UUID getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(UUID organizationId) {
            this.organizationId = organizationId;
        }

        public String getHireDateFrom() {
            return hireDateFrom;
        }

        public void setHireDateFrom(String hireDateFrom) {
            this.hireDateFrom = hireDateFrom;
        }

        public String getHireDateTo() {
            return hireDateTo;
        }

        public void setHireDateTo(String hireDateTo) {
            this.hireDateTo = hireDateTo;
        }
    }
}
