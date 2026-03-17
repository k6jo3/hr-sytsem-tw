package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.CreateLegalDeductionContext;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存法扣款任務
 */
@Component
@RequiredArgsConstructor
public class SaveLegalDeductionTask implements PipelineTask<CreateLegalDeductionContext> {

    private final ILegalDeductionRepository repository;

    @Override
    public void execute(CreateLegalDeductionContext context) {
        if (context.getLegalDeduction() != null) {
            repository.save(context.getLegalDeduction());
        }
    }

    @Override
    public String getName() {
        return "儲存法扣款";
    }
}
