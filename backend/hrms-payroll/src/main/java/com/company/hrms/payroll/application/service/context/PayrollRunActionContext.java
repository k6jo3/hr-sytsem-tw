package com.company.hrms.payroll.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 薪資批次操作 Context
 * 用於 Submit/Approve/Reject/Paid 等操作
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PayrollRunActionContext extends PipelineContext {

    // === 輸入 ===
    private final PayrollRunActionRequest request;
    private final JWTModel currentUser;
    private final String actionType;

    // === 中間資料 ===
    private PayrollRun payrollRun;

    public PayrollRunActionContext(PayrollRunActionRequest request, JWTModel currentUser, String actionType) {
        this.request = request;
        this.currentUser = currentUser;
        this.actionType = actionType;
    }
}
