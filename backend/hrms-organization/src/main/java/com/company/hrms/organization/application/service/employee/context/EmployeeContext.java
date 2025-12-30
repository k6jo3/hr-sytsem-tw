package com.company.hrms.organization.application.service.employee.context;

import java.time.LocalDate;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.organization.api.request.employee.CreateEmployeeRequest;
import com.company.hrms.organization.api.request.employee.PromoteEmployeeRequest;
import com.company.hrms.organization.api.request.employee.TerminateEmployeeRequest;
import com.company.hrms.organization.api.request.employee.TransferEmployeeRequest;
import com.company.hrms.organization.api.request.employee.UpdateEmployeeRequest;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 員工 Pipeline Context
 * 在 Pipeline 各 Task 間傳遞員工操作相關資料
 */
@Getter
@Setter
@NoArgsConstructor
public class EmployeeContext extends PipelineContext {

    // === 輸入：員工 ID ===
    private String employeeId;
    private String tenantId;

    // === 輸入：各種 Request ===
    private CreateEmployeeRequest createRequest;
    private UpdateEmployeeRequest updateRequest;
    private TransferEmployeeRequest transferRequest;
    private PromoteEmployeeRequest promoteRequest;
    private TerminateEmployeeRequest terminateRequest;

    // === 中間數據 ===
    private Employee employee;
    private Department oldDepartment;
    private Department newDepartment;
    private EmployeeHistory history;

    // === 輸出 ===
    private String resultEmployeeId;
    private String resultMessage;

    // ==================== 建構子 ====================

    /**
     * 單純依 ID 查詢/操作員工
     */
    public EmployeeContext(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * 部門調動
     */
    public EmployeeContext(String employeeId, TransferEmployeeRequest request) {
        this.employeeId = employeeId;
        this.transferRequest = request;
    }

    /**
     * 升遷
     */
    public EmployeeContext(String employeeId, PromoteEmployeeRequest request) {
        this.employeeId = employeeId;
        this.promoteRequest = request;
    }

    /**
     * 離職
     */
    public EmployeeContext(String employeeId, TerminateEmployeeRequest request) {
        this.employeeId = employeeId;
        this.terminateRequest = request;
    }

    /**
     * 新增員工
     */
    public EmployeeContext(CreateEmployeeRequest request, String tenantId) {
        this.createRequest = request;
        this.tenantId = tenantId;
    }

    /**
     * 更新員工
     */
    public EmployeeContext(String employeeId, UpdateEmployeeRequest request) {
        this.employeeId = employeeId;
        this.updateRequest = request;
    }

    // ==================== 便利方法 ====================

    /**
     * 取得調動生效日期
     */
    public LocalDate getTransferEffectiveDate() {
        return transferRequest != null ? transferRequest.getEffectiveDate() : null;
    }

    /**
     * 取得新部門 ID
     */
    public String getNewDepartmentId() {
        return transferRequest != null ? transferRequest.getNewDepartmentId() : null;
    }

    /**
     * 取得舊部門 ID
     */
    public String getOldDepartmentId() {
        return employee != null && employee.getDepartmentId() != null
                ? employee.getDepartmentId().toString()
                : null;
    }
}
