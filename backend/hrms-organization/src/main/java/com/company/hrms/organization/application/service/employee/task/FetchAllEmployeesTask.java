package com.company.hrms.organization.application.service.employee.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.employee.context.EmployeeExportContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢所有員工 Task
 */
@Component
@RequiredArgsConstructor
public class FetchAllEmployeesTask implements PipelineTask<EmployeeExportContext> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(EmployeeExportContext context) throws Exception {
        List<Employee> employees = employeeRepository.findAll();
        context.setEmployees(employees);
    }

    @Override
    public String getName() {
        return "查詢所有員工";
    }
}
