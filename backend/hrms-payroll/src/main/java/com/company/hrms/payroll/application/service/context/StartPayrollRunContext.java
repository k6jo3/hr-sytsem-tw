package com.company.hrms.payroll.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.StartPayrollRunRequest;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 啟動薪資計算 Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StartPayrollRunContext extends PipelineContext {
    private final StartPayrollRunRequest request;
    private final JWTModel currentUser;

    private PayrollRun payrollRun;

    public StartPayrollRunContext(StartPayrollRunRequest request, JWTModel currentUser) {
        this.request = request;
        this.currentUser = currentUser;
    }
}
