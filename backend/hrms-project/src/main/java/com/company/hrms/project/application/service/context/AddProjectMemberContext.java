package com.company.hrms.project.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.project.api.request.AddProjectMemberRequest;
import com.company.hrms.project.domain.model.aggregate.Project;

import lombok.Getter;
import lombok.Setter;

/**
 * 新增專案成員 Context
 */
@Getter
@Setter
public class AddProjectMemberContext extends PipelineContext {

    private AddProjectMemberRequest request;
    private Project project;

    public AddProjectMemberContext(AddProjectMemberRequest request) {
        this.request = request;
    }
}
