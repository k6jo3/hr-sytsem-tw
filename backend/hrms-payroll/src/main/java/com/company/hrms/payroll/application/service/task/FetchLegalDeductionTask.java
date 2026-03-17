package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.application.service.context.LegalDeductionActionContext;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入法扣款任務（用於狀態操作）
 */
@Component
@RequiredArgsConstructor
public class FetchLegalDeductionTask implements PipelineTask<LegalDeductionActionContext> {

    private final ILegalDeductionRepository repository;

    @Override
    public void execute(LegalDeductionActionContext context) {
        String deductionId = context.getRequest().getDeductionId();
        if (deductionId == null || deductionId.isBlank()) {
            throw new IllegalArgumentException("法扣款 ID 為必填");
        }

        LegalDeduction deduction = repository.findById(new DeductionId(deductionId))
                .orElseThrow(() -> new DomainException("LEGAL_DEDUCTION_NOT_FOUND",
                        "找不到法扣款: " + deductionId));

        context.setLegalDeduction(deduction);
    }

    @Override
    public String getName() {
        return "載入法扣款";
    }
}
