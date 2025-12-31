package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.PayrollRunActionContext;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;

import lombok.RequiredArgsConstructor;

/**
 * 執行薪資批次操作任務
 * 根據 actionType 執行對應的狀態轉換
 */
@Component
@RequiredArgsConstructor
public class ExecutePayrollRunActionTask implements PipelineTask<PayrollRunActionContext> {

    private final IPayrollRunRepository repository;

    @Override
    public void execute(PayrollRunActionContext context) {
        String userId = context.getCurrentUser() != null ? context.getCurrentUser().getUserId() : "SYSTEM";
        String action = context.getActionType();

        switch (action) {
            case "SUBMIT":
                context.getPayrollRun().submit(userId);
                break;
            case "APPROVE":
                context.getPayrollRun().approve(userId);
                break;
            case "REJECT":
                context.getPayrollRun().reject(userId);
                break;
            case "MARK_PAID":
                String bankFileUrl = context.getRequest().getBankFileUrl();
                context.getPayrollRun().markAsPaid(bankFileUrl);
                break;
            default:
                throw new IllegalArgumentException("不支援的操作類型: " + action);
        }

        repository.save(context.getPayrollRun());
    }

    @Override
    public String getName() {
        return "ExecutePayrollRunActionTask";
    }
}
