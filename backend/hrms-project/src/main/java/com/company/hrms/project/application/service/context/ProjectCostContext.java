package com.company.hrms.project.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.project.api.request.GetProjectCostRequest;
import com.company.hrms.project.api.response.GetProjectCostResponse;
import com.company.hrms.project.domain.model.aggregate.Project;

import lombok.Getter;
import lombok.Setter;

/**
 * 專案成本分析 Context
 */
@Getter
@Setter
public class ProjectCostContext extends PipelineContext {

    private GetProjectCostRequest request;
    private Project project;
    private GetProjectCostResponse response;

    public ProjectCostContext(GetProjectCostRequest request) {
        this.request = request;
    }
}
