package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.dto.request.CreateLegalDeductionRequest;
import com.company.hrms.payroll.application.service.context.CreateLegalDeductionContext;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentType;

import lombok.RequiredArgsConstructor;

/**
 * 初始化法扣款任務
 * 根據請求建立法扣款聚合根
 */
@Component
@RequiredArgsConstructor
public class InitLegalDeductionTask implements PipelineTask<CreateLegalDeductionContext> {

    @Override
    public void execute(CreateLegalDeductionContext context) {
        CreateLegalDeductionRequest request = context.getRequest();

        GarnishmentType type = GarnishmentType.valueOf(request.getGarnishmentType());
        int priority = request.getPriority() != null ? request.getPriority() : 1;

        LegalDeduction deduction = new LegalDeduction(
                DeductionId.generate(),
                request.getEmployeeId(),
                request.getCourtOrderNumber(),
                type,
                request.getTotalAmount(),
                priority,
                request.getEffectiveDate(),
                request.getIssuingAuthority());

        // 設定可選欄位
        if (request.getExpiryDate() != null) {
            deduction.setExpiryDate(request.getExpiryDate());
        }
        if (request.getCaseNumber() != null) {
            deduction.setCaseNumber(request.getCaseNumber());
        }
        if (request.getNote() != null) {
            deduction.setNote(request.getNote());
        }

        context.setLegalDeduction(deduction);
    }

    @Override
    public String getName() {
        return "初始化法扣款";
    }
}
