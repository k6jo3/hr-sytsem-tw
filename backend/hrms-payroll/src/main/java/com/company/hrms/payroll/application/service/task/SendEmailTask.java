package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.PayslipActionContext;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發送薪資單 Email 任務
 * 呼叫 Notification Service 發送郵件
 * 
 * 注意：外部服務整合需在 hrms-notification 模組實作後注入
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendEmailTask implements PipelineTask<PayslipActionContext> {

    private final IPayslipRepository payslipRepository;
    // 外部服務整合點：當 hrms-notification 模組完成後，注入 NotificationServiceClient
    // private final NotificationServiceClient notificationServiceClient;

    @Override
    public void execute(PayslipActionContext context) {
        // 模擬 Email 發送 - 實際實作時呼叫 Notification Service
        // notificationServiceClient.sendPayslipEmail(
        // context.getPayslip().getEmployeeId(),
        // context.getPayslip().getPdfUrl()
        // );

        context.getPayslip().markAsSent();
        payslipRepository.save(context.getPayslip());

        context.setEmailSent(true);
        log.info("已發送薪資單 Email 給員工: {}", context.getPayslip().getEmployeeId());
    }

    @Override
    public String getName() {
        return "SendEmailTask";
    }
}
