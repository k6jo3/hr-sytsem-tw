package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.LegalDeductionActionContext;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 執行法扣款狀態操作任務
 * 根據 actionType 執行暫停、恢復、終止
 */
@Component
@RequiredArgsConstructor
public class ExecuteLegalDeductionActionTask implements PipelineTask<LegalDeductionActionContext> {

    private final ILegalDeductionRepository repository;

    @Override
    public void execute(LegalDeductionActionContext context) {
        String action = context.getActionType();

        switch (action) {
            case "SUSPEND":
                context.getLegalDeduction().suspend();
                break;
            case "RESUME":
                context.getLegalDeduction().resume();
                break;
            case "TERMINATE":
                context.getLegalDeduction().terminate();
                break;
            default:
                throw new IllegalArgumentException("不支援的法扣款操作類型: " + action);
        }

        repository.save(context.getLegalDeduction());
    }

    @Override
    public String getName() {
        return "執行法扣款狀態操作";
    }
}
