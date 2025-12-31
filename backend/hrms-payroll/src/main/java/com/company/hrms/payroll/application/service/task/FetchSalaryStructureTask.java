package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.application.service.context.UpdateSalaryStructureContext;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.valueobject.StructureId;
import com.company.hrms.payroll.domain.repository.ISalaryStructureRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入薪資結構任務
 */
@Component
@RequiredArgsConstructor
public class FetchSalaryStructureTask implements PipelineTask<UpdateSalaryStructureContext> {

    private final ISalaryStructureRepository repository;

    @Override
    public void execute(UpdateSalaryStructureContext context) {
        if (context.getStructureId() == null) {
            throw new IllegalArgumentException("結構 ID 為必填");
        }

        SalaryStructure structure = repository.findById(new StructureId(context.getStructureId()))
                .orElseThrow(() -> new DomainException("SALARY_STRUCTURE_NOT_FOUND",
                        "找不到薪資結構: " + context.getStructureId()));

        context.setSalaryStructure(structure);
    }

    @Override
    public String getName() {
        return "FetchSalaryStructureTask";
    }
}
