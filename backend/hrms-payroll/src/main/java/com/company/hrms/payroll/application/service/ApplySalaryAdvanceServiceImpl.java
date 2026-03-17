package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.ApplySalaryAdvanceRequest;
import com.company.hrms.payroll.application.dto.response.SalaryAdvanceResponse;
import com.company.hrms.payroll.application.factory.SalaryAdvanceDtoFactory;
import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.application.service.task.InitSalaryAdvanceTask;
import com.company.hrms.payroll.application.service.task.SaveSalaryAdvanceTask;

import lombok.RequiredArgsConstructor;

/**
 * 預借薪資申請服務
 * 使用 Pipeline 模式編排：初始化 → 儲存
 */
@Service("applySalaryAdvanceServiceImpl")
@Transactional
@RequiredArgsConstructor
public class ApplySalaryAdvanceServiceImpl
        extends AbstractCommandService<ApplySalaryAdvanceRequest, SalaryAdvanceResponse> {

    private final InitSalaryAdvanceTask initSalaryAdvanceTask;
    private final SaveSalaryAdvanceTask saveSalaryAdvanceTask;

    @Override
    protected SalaryAdvanceResponse doExecute(ApplySalaryAdvanceRequest request, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 建立 Context
        SalaryAdvanceContext context = new SalaryAdvanceContext();
        context.setCurrentUser(currentUser);
        context.setActionType("APPLY");
        context.setEmployeeId(request.getEmployeeId());
        context.setRequestedAmount(request.getRequestedAmount());
        context.setInstallmentMonths(request.getInstallmentMonths());
        context.setReason(request.getReason());

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(initSalaryAdvanceTask)
                .next(saveSalaryAdvanceTask)
                .execute();

        // 3. 回傳結果
        return SalaryAdvanceDtoFactory.toResponse(context.getSalaryAdvance());
    }
}
