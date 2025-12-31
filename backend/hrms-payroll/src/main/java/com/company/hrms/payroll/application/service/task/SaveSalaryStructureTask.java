package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.CreateSalaryStructureContext;
import com.company.hrms.payroll.domain.repository.ISalaryStructureRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存薪資結構任務
 */
@Component
@RequiredArgsConstructor
public class SaveSalaryStructureTask implements PipelineTask<CreateSalaryStructureContext> {

    private final ISalaryStructureRepository repository;

    @Override
    public void execute(CreateSalaryStructureContext context) {
        if (context.getSalaryStructure() != null) {
            repository.save(context.getSalaryStructure());
        }
    }

    @Override
    public String getName() {
        return "SaveSalaryStructureTask";
    }
}
