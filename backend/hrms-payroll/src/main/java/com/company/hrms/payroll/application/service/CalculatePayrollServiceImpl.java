package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.CalculatePayrollRequest;
import com.company.hrms.payroll.application.dto.response.PayrollRunResponse;
import com.company.hrms.payroll.application.factory.PayrollRunDtoFactory;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.application.service.task.CalculatePayslipsTask;
import com.company.hrms.payroll.application.service.task.CompleteExecutionTask;
import com.company.hrms.payroll.application.service.task.FetchEligibleStructuresTask;
import com.company.hrms.payroll.application.service.task.FetchPayrollRunTask;
import com.company.hrms.payroll.application.service.task.StartExecutionTask;

import lombok.RequiredArgsConstructor;

/**
 * 計算薪資服務
 * 使用 Pipeline 模式編排薪資計算流程
 */
@Service("calculatePayrollServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CalculatePayrollServiceImpl extends AbstractCommandService<CalculatePayrollRequest, PayrollRunResponse> {

    private final FetchPayrollRunTask fetchPayrollRunTask;
    private final FetchEligibleStructuresTask fetchEligibleStructuresTask;
    private final StartExecutionTask startExecutionTask;
    private final CalculatePayslipsTask calculatePayslipsTask;
    private final CompleteExecutionTask completeExecutionTask;

    @Override
    protected PayrollRunResponse doExecute(CalculatePayrollRequest request, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 建立 Context
        CalculatePayrollContext context = new CalculatePayrollContext(request, currentUser);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(fetchPayrollRunTask) // 載入批次
                .next(fetchEligibleStructuresTask) // 載入符合條件的薪資結構
                .next(startExecutionTask) // 開始執行
                .next(calculatePayslipsTask) // 計算薪資單
                .next(completeExecutionTask) // 完成並彙總統計
                .execute();

        // 3. 回傳結果
        return PayrollRunDtoFactory.toResponse(context.getPayrollRun());
    }
}
