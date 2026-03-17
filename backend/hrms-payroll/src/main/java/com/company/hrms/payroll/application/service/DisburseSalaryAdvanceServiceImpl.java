package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.response.SalaryAdvanceResponse;
import com.company.hrms.payroll.application.factory.SalaryAdvanceDtoFactory;
import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.application.service.task.ExecuteSalaryAdvanceActionTask;
import com.company.hrms.payroll.application.service.task.LoadSalaryAdvanceTask;
import com.company.hrms.payroll.application.service.task.SaveSalaryAdvanceTask;

import lombok.RequiredArgsConstructor;

/**
 * 撥款預借薪資服務
 * 使用 Pipeline 模式編排：載入 → 執行撥款 → 儲存
 *
 * 注意：disburse 不需要 request body，使用 String 作為 request type 佔位
 */
@Service("disburseSalaryAdvanceServiceImpl")
@Transactional
@RequiredArgsConstructor
public class DisburseSalaryAdvanceServiceImpl
        extends AbstractCommandService<String, SalaryAdvanceResponse> {

    private final LoadSalaryAdvanceTask loadSalaryAdvanceTask;
    private final ExecuteSalaryAdvanceActionTask executeSalaryAdvanceActionTask;
    private final SaveSalaryAdvanceTask saveSalaryAdvanceTask;

    @Override
    protected SalaryAdvanceResponse doExecute(String request, JWTModel currentUser, String... args) throws Exception {

        // 1. 建立 Context
        SalaryAdvanceContext context = new SalaryAdvanceContext();
        context.setAdvanceId(args != null && args.length > 0 ? args[0] : null);
        context.setCurrentUser(currentUser);
        context.setActionType("DISBURSE");

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadSalaryAdvanceTask)
                .next(executeSalaryAdvanceActionTask)
                .next(saveSalaryAdvanceTask)
                .execute();

        // 3. 回傳結果
        return SalaryAdvanceDtoFactory.toResponse(context.getSalaryAdvance());
    }
}
