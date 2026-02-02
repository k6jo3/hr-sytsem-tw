package com.company.hrms.project.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.project.application.service.context.ProjectCostContext;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入專案詳細資訊 Task (成本分析用)
 */
@Component
@RequiredArgsConstructor
public class LoadProjectForCostTask implements PipelineTask<ProjectCostContext> {

    private final IProjectRepository projectRepository;

    @Override
    public void execute(ProjectCostContext context) throws Exception {
        String projectIdStr = context.getRequest().getProjectId();
        if (projectIdStr == null) {
            throw new IllegalArgumentException("Project ID is required");
        }

        var project = projectRepository.findById(new ProjectId(projectIdStr))
                .orElseThrow(() -> new DomainException("專案不存在: " + projectIdStr));

        context.setProject(project);
    }

    @Override
    public String getName() {
        return "載入專案詳細資訊";
    }
}
