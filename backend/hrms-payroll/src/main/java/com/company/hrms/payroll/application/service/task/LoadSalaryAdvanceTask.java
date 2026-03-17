package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.repository.ISalaryAdvanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入預借薪資資料 Task
 * 從 Repository 查詢既有的預借記錄
 */
@Component
@RequiredArgsConstructor
public class LoadSalaryAdvanceTask implements PipelineTask<SalaryAdvanceContext> {

    private final ISalaryAdvanceRepository repository;

    @Override
    public void execute(SalaryAdvanceContext context) throws Exception {
        String advanceId = context.getAdvanceId();
        if (advanceId == null || advanceId.isBlank()) {
            throw new IllegalArgumentException("預借 ID 不可為空");
        }

        context.setSalaryAdvance(
                repository.findById(new AdvanceId(advanceId))
                        .orElseThrow(() -> new DomainException(
                                "SALARY_ADVANCE_NOT_FOUND",
                                "找不到預借薪資記錄: " + advanceId)));
    }

    @Override
    public String getName() {
        return "載入預借薪資資料";
    }
}
