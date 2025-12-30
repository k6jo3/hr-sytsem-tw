package com.company.hrms.organization.application.service.department.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.organization.api.request.department.AssignManagerRequest;
import com.company.hrms.organization.api.request.department.CreateDepartmentRequest;
import com.company.hrms.organization.api.request.department.UpdateDepartmentRequest;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 部門 Pipeline Context
 */
@Getter
@Setter
@NoArgsConstructor
public class DepartmentContext extends PipelineContext {

    // === 輸入 ===
    private CreateDepartmentRequest createRequest;
    private UpdateDepartmentRequest updateRequest;
    private AssignManagerRequest assignManagerRequest;
    private String departmentId;

    // === 中間數據 ===
    private Department department;
    private Department parentDepartment;
    private DepartmentId parentId;
    private OrganizationId organizationId;
    private Employee manager;

    // === 統計資訊 ===
    private int employeeCount;
    private int childDepartmentCount;
    private String organizationName;
    private String parentName;
    private String managerName;

    // === 建構子 ===

    public DepartmentContext(CreateDepartmentRequest request) {
        this.createRequest = request;
    }

    public DepartmentContext(String departmentId) {
        this.departmentId = departmentId;
    }

    public DepartmentContext(String departmentId, UpdateDepartmentRequest request) {
        this.departmentId = departmentId;
        this.updateRequest = request;
    }

    public DepartmentContext(String departmentId, AssignManagerRequest request) {
        this.departmentId = departmentId;
        this.assignManagerRequest = request;
    }
}
