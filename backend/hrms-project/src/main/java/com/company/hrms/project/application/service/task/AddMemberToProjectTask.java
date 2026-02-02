package com.company.hrms.project.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.project.application.service.context.AddProjectMemberContext;
import com.company.hrms.project.domain.model.aggregate.Project;

/**
 * 新增專案成員操作 Task
 */
@Component
public class AddMemberToProjectTask implements PipelineTask<AddProjectMemberContext> {

    @Override
    public void execute(AddProjectMemberContext context) throws Exception {
        Project project = context.getProject();
        var req = context.getRequest();

        project.addMember(req.getEmployeeId(), req.getRole(), req.getAllocatedHours());
    }

    @Override
    public String getName() {
        return "新增專案成員";
    }
}
