package com.company.hrms.insurance.application.service.groupplan.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.insurance.api.request.CreateGroupInsurancePlanRequest;
import com.company.hrms.insurance.domain.model.aggregate.GroupInsurancePlan;

import lombok.Getter;
import lombok.Setter;

/**
 * 建立團體保險方案 Pipeline Context
 */
@Getter
@Setter
public class CreateGroupPlanContext extends PipelineContext {

    // === 輸入 ===
    private final CreateGroupInsurancePlanRequest request;

    // === 輸出 ===
    private GroupInsurancePlan plan;

    public CreateGroupPlanContext(CreateGroupInsurancePlanRequest request) {
        this.request = request;
    }
}
