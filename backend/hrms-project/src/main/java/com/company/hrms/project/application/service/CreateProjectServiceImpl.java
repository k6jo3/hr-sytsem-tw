package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.CreateProjectRequest;
import com.company.hrms.project.api.response.CreateProjectResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.command.CreateProjectCommand;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

@Service("createProjectServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CreateProjectServiceImpl implements CommandApiService<CreateProjectRequest, CreateProjectResponse> {

    private final IProjectRepository projectRepository;
    private final EventPublisher eventPublisher;

    @Override
    public CreateProjectResponse execCommand(CreateProjectRequest req, JWTModel currentUser, String... args)
            throws Exception {
        CreateProjectCommand cmd = req.toCommand();
        // Set audit user in command if needed? Or AggregateRoot should handle specific
        // audit user passing?
        // AggregateRoot handles timestamps, but createdBy usually comes from somewhere.
        // For now, simple implementation logic.

        Project project = Project.create(cmd);
        projectRepository.save(project);

        eventPublisher.publishAll(project.getDomainEvents());
        project.clearDomainEvents();

        return new CreateProjectResponse(project.getId().getValue());
    }
}
