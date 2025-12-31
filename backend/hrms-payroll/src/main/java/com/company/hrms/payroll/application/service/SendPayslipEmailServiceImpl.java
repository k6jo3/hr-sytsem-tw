package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.application.dto.response.PayslipResponse;
import com.company.hrms.payroll.application.factory.PayslipDtoFactory;
import com.company.hrms.payroll.application.service.context.PayslipActionContext;
import com.company.hrms.payroll.application.service.task.FetchPayslipTask;
import com.company.hrms.payroll.application.service.task.SendEmailTask;

import lombok.RequiredArgsConstructor;

/**
 * 發送薪資單 Email 服務
 * 使用 Pipeline 模式編排 Email 發送流程
 * 
 * 外部服務整合說明：
 * - 實際 Email 發送邏輯在 SendEmailTask 中
 * - 需在 hrms-notification 模組完成後注入 NotificationServiceClient
 */
@Service("sendPayslipEmailServiceImpl")
@Transactional
@RequiredArgsConstructor
public class SendPayslipEmailServiceImpl extends AbstractCommandService<PayrollRunActionRequest, PayslipResponse> {

    private final FetchPayslipTask fetchPayslipTask;
    private final SendEmailTask sendEmailTask;

    @Override
    protected PayslipResponse doExecute(PayrollRunActionRequest request, JWTModel currentUser, String... args)
            throws Exception {

        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("薪資單 ID 為必填");
        }
        String payslipId = args[0];

        // 1. 建立 Context
        PayslipActionContext context = new PayslipActionContext(payslipId, currentUser, "SEND_EMAIL");

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(fetchPayslipTask)
                .next(sendEmailTask)
                .execute();

        // 3. 回傳結果
        return PayslipDtoFactory.toResponse(context.getPayslip());
    }
}
