package com.company.hrms.payroll.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 刪除薪資結構 Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteSalaryStructureContext extends PipelineContext {

    // === 輸入 ===
    private final String structureId;
    private final JWTModel currentUser;

    // === 中間資料 ===
    private SalaryStructure salaryStructure;

    public DeleteSalaryStructureContext(String structureId, JWTModel currentUser) {
        this.structureId = structureId;
        this.currentUser = currentUser;
    }
}
