package com.company.hrms.organization.application.service.employee.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 載入員工 Task (Infrastructure Task)
 * 根據 employeeId 從 Repository 載入 Employee 聚合根
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadEmployeeTask implements PipelineTask<EmployeeContext> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        String employeeId = context.getEmployeeId();
        log.debug("載入員工: {}", employeeId);

        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeId));

        context.setEmployee(employee);
        log.debug("員工載入成功: {} - {}", employee.getEmployeeNumber(), employee.getFullName());
    }

    @Override
    public String getName() {
        return "載入員工資料";
    }
}
