package com.company.hrms.payroll.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.CreateSalaryStructureRequest;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 建立薪資結構 Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CreateSalaryStructureContext extends PipelineContext {

    // === 輸入 ===
    private final CreateSalaryStructureRequest request;
    private final JWTModel currentUser;

    // === 輸出 ===
    private SalaryStructure salaryStructure;

    public CreateSalaryStructureContext(CreateSalaryStructureRequest request, JWTModel currentUser) {
        this.request = request;
        this.currentUser = currentUser;
    }
}
