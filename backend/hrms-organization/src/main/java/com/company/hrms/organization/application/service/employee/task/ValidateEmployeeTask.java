package com.company.hrms.organization.application.service.employee.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.CreateEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 驗證員工資料 Task (Domain Task)
 * 驗證員工編號、Email、身分證號唯一性，以及部門是否存在
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateEmployeeTask implements PipelineTask<EmployeeContext> {

    private final IEmployeeRepository employeeRepository;
    private final IDepartmentRepository departmentRepository;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        CreateEmployeeRequest request = context.getCreateRequest();
        log.debug("驗證員工資料: {}", request.getEmployeeNumber());

        // 驗證員工編號唯一性
        if (employeeRepository.existsByEmployeeNumber(request.getEmployeeNumber())) {
            throw new IllegalArgumentException("員工編號已存在: " + request.getEmployeeNumber());
        }

        // 驗證 Email 唯一性
        if (employeeRepository.existsByEmail(request.getCompanyEmail())) {
            throw new IllegalArgumentException("Email 已存在: " + request.getCompanyEmail());
        }

        // 驗證身分證號唯一性
        if (request.getNationalId() != null &&
                employeeRepository.existsByNationalId(request.getNationalId())) {
            throw new IllegalArgumentException("身分證號已存在");
        }

        // 驗證部門存在
        DepartmentId departmentId = new DepartmentId(request.getDepartmentId());
        if (!departmentRepository.existsById(departmentId)) {
            throw new IllegalArgumentException("部門不存在: " + request.getDepartmentId());
        }

        log.debug("員工資料驗證通過: {}", request.getEmployeeNumber());
    }

    @Override
    public String getName() {
        return "驗證員工資料";
    }

    @Override
    public boolean shouldExecute(EmployeeContext context) {
        return context.getCreateRequest() != null;
    }
}
