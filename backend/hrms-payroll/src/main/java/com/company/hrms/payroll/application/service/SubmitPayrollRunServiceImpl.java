package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.application.dto.response.PayrollRunResponse;
import com.company.hrms.payroll.application.factory.PayrollRunDtoFactory;
import com.company.hrms.payroll.application.service.context.PayrollRunActionContext;
import com.company.hrms.payroll.application.service.task.ExecutePayrollRunActionTask;
import com.company.hrms.payroll.application.service.task.FetchPayrollRunForActionTask;

import lombok.RequiredArgsConstructor;

/**
 * 提交薪資批次服務
 */
@Service("submitPayrollRunServiceImpl")
@Transactional
@RequiredArgsConstructor
public class SubmitPayrollRunServiceImpl extends AbstractCommandService<PayrollRunActionRequest, PayrollRunResponse> {

    private final FetchPayrollRunForActionTask fetchTask;
    private final ExecutePayrollRunActionTask executeTask;

    @Override
    protected PayrollRunResponse doExecute(PayrollRunActionRequest request, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 建立 Context
        PayrollRunActionContext context = new PayrollRunActionContext(request, currentUser, "SUBMIT");

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(fetchTask)
                .next(executeTask)
                .execute();

        // 3. 回傳結果
        return PayrollRunDtoFactory.toResponse(context.getPayrollRun());
    }
}
