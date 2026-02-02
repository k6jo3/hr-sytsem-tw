package com.company.hrms.project.application.service.context;

import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.project.api.request.GetWBSTreeRequest;
import com.company.hrms.project.api.response.GetWBSTreeResponse;
import com.company.hrms.project.domain.model.aggregate.Task;

import lombok.Getter;
import lombok.Setter;

/**
 * WBS 結構查詢 Context
 */
@Getter
@Setter
public class WBSTreeContext extends PipelineContext {

    private GetWBSTreeRequest request;
    private String projectId;

    private List<Task> allTasks;
    private GetWBSTreeResponse response;

    public WBSTreeContext(GetWBSTreeRequest request, String projectId) {
        this.request = request;
        this.projectId = projectId;
    }
}
