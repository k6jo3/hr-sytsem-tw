package com.company.hrms.payroll.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.UpdateSalaryStructureRequest;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新薪資結構 Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateSalaryStructureContext extends PipelineContext {

    // === 輸入 ===
    private final UpdateSalaryStructureRequest request;
    private final JWTModel currentUser;
    private final String structureId;

    // === 中間資料 ===
    private SalaryStructure salaryStructure;

    public UpdateSalaryStructureContext(String structureId, UpdateSalaryStructureRequest request,
            JWTModel currentUser) {
        this.structureId = structureId;
        this.request = request;
        this.currentUser = currentUser;
    }
}
