package com.company.hrms.organization.application.service.employee.context;

import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.Getter;
import lombok.Setter;

/**
 * 員工匯出 Context
 */
@Getter
@Setter
public class EmployeeExportContext extends PipelineContext {

    private List<Employee> employees;
    private byte[] result;

}
