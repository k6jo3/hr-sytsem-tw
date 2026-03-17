package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.ApproveSalaryAdvanceRequest;
import com.company.hrms.payroll.application.dto.response.SalaryAdvanceResponse;
import com.company.hrms.payroll.application.factory.SalaryAdvanceDtoFactory;
import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.application.service.task.ExecuteSalaryAdvanceActionTask;
import com.company.hrms.payroll.application.service.task.LoadSalaryAdvanceTask;
import com.company.hrms.payroll.application.service.task.SaveSalaryAdvanceTask;

import lombok.RequiredArgsConstructor;

/**
 * 核准預借薪資服務
 * 使用 Pipeline 模式編排：載入 → 執行核准 → 儲存
 */
@Service("approveSalaryAdvanceServiceImpl")
@Transactional
@RequiredArgsConstructor
public class ApproveSalaryAdvanceServiceImpl
        extends AbstractCommandService<ApproveSalaryAdvanceRequest, SalaryAdvanceResponse> {

    private final LoadSalaryAdvanceTask loadSalaryAdvanceTask;
    private final ExecuteSalaryAdvanceActionTask executeSalaryAdvanceActionTask;
    private final SaveSalaryAdvanceTask saveSalaryAdvanceTask;

    @Override
    protected SalaryAdvanceResponse doExecute(ApproveSalaryAdvanceRequest request, JWTModel currentUser,
            String... args) throws Exception {

        // 1. 建立 Context（advanceId 從 path variable 取得）
        SalaryAdvanceContext context = new SalaryAdvanceContext();
        context.setAdvanceId(args != null && args.length > 0 ? args[0] : null);
        context.setCurrentUser(currentUser);
        context.setActionType("APPROVE");
        context.setApprovedAmount(request.getApprovedAmount());

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
