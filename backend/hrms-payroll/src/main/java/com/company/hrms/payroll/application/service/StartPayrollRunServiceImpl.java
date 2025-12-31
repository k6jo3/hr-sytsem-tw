package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.StartPayrollRunRequest;
import com.company.hrms.payroll.application.dto.response.PayrollRunResponse;
import com.company.hrms.payroll.application.factory.PayrollRunDtoFactory;
import com.company.hrms.payroll.application.service.context.StartPayrollRunContext;
import com.company.hrms.payroll.application.service.task.InitPayrollRunTask;
import com.company.hrms.payroll.application.service.task.SavePayrollRunTask;
import com.company.hrms.payroll.domain.event.PayrollRunStartedEvent;

import lombok.RequiredArgsConstructor;

/**
 * 啟動薪資計算服務
 */
@Service("startPayrollRunServiceImpl")
@Transactional
@RequiredArgsConstructor
public class StartPayrollRunServiceImpl extends AbstractCommandService<StartPayrollRunRequest, PayrollRunResponse> {

    private final InitPayrollRunTask initPayrollRunTask;
    private final SavePayrollRunTask savePayrollRunTask;

    @Override
    protected PayrollRunResponse doExecute(StartPayrollRunRequest request, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 建立 Context
        StartPayrollRunContext context = new StartPayrollRunContext(request, currentUser);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(initPayrollRunTask)
                .next(savePayrollRunTask)
                .execute();

        // 3. 發布事件
        if (context.getPayrollRun() != null) {
            registerEvent(new PayrollRunStartedEvent(
                    context.getPayrollRun().getId().getValue(),
                    context.getPayrollRun().getOrganizationId(),
                    java.time.LocalDateTime.now()));
        }

        // 4. 回傳結果
        return PayrollRunDtoFactory.toResponse(context.getPayrollRun());
    }
}
