package com.company.hrms.project.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.project.application.service.context.AddProjectMemberContext;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存專案 Task (含發布事件)
 */
@Component
@RequiredArgsConstructor
public class SaveProjectTask implements PipelineTask<AddProjectMemberContext> {

    private final IProjectRepository projectRepository;
    private final EventPublisher eventPublisher;

    @Override
    public void execute(AddProjectMemberContext context) throws Exception {
        Project project = context.getProject();

        projectRepository.save(project);

        eventPublisher.publishAll(project.getDomainEvents());
        project.clearDomainEvents();
    }

    @Override
    public String getName() {
        return "儲存專案";
    }
}
