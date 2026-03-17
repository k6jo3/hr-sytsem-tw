package com.company.hrms.payroll.application.service.task;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;

import lombok.RequiredArgsConstructor;

/**
 * 執行預借薪資操作 Task
 * 根據 actionType 執行對應的 Domain 行為
 */
@Component
@RequiredArgsConstructor
public class ExecuteSalaryAdvanceActionTask implements PipelineTask<SalaryAdvanceContext> {

    @Override
    public void execute(SalaryAdvanceContext context) throws Exception {
        SalaryAdvance advance = context.getSalaryAdvance();
        String actionType = context.getActionType();

        switch (actionType) {
            case "APPROVE":
                String approverId = context.getCurrentUser().getUserId();
                advance.approve(approverId, context.getApprovedAmount());
                break;
            case "REJECT":
                String rejecterId = context.getCurrentUser().getUserId();
                advance.reject(rejecterId, context.getRejectionReason());
                break;
            case "DISBURSE":
                advance.disburse(LocalDate.now());
                break;
            case "CANCEL":
                advance.cancel();
                break;
            default:
                throw new IllegalArgumentException("不支援的操作類型: " + actionType);
        }
    }

    @Override
    public String getName() {
        return "執行預借薪資操作";
    }
}
