package com.company.hrms.organization.application.service.employee.context;

import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Getter;
import lombok.Setter;

/**
 * 員工匯入 Context
 */
@Getter
@Setter
public class EmployeeImportContext extends PipelineContext {
    private String fileName;
    private int totalCount;
    private int successCount;
    private int failureCount;
}
