package com.company.hrms.project.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.project.application.service.context.AddProjectMemberContext;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入專案 Task
 */
@Component
@RequiredArgsConstructor
public class LoadProjectTask implements PipelineTask<AddProjectMemberContext> {

    private final IProjectRepository projectRepository;

    @Override
    public void execute(AddProjectMemberContext context) throws Exception {
        String projectIdStr = context.getRequest().getProjectId();
        if (projectIdStr == null) {
            throw new IllegalArgumentException("Project ID is required");
        }

        var project = projectRepository.findById(new ProjectId(projectIdStr))
                .orElseThrow(() -> new DomainException("Project not found: " + projectIdStr));

        context.setProject(project);
    }

    @Override
    public String getName() {
        return "載入專案";
    }
}
