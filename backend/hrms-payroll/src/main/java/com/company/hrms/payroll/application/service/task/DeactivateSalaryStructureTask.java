package com.company.hrms.payroll.application.service.task;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.DeleteSalaryStructureContext;
import com.company.hrms.payroll.domain.repository.ISalaryStructureRepository;

import lombok.RequiredArgsConstructor;

/**
 * 執行薪資結構停用任務（邏輯刪除）
 */
@Component
@RequiredArgsConstructor
public class DeactivateSalaryStructureTask implements PipelineTask<DeleteSalaryStructureContext> {

    private final ISalaryStructureRepository repository;

    @Override
    public void execute(DeleteSalaryStructureContext context) {
        // 邏輯刪除 - 設定結束日期並停用
        context.getSalaryStructure().deactivate(LocalDate.now());
        repository.save(context.getSalaryStructure());
    }

    @Override
    public String getName() {
        return "DeactivateSalaryStructureTask";
    }
}
