package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.AddProjectMemberRequest;
import com.company.hrms.project.api.response.AddProjectMemberResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

@Service("addProjectMemberServiceImpl")
@RequiredArgsConstructor
@Transactional
public class AddProjectMemberServiceImpl
        implements CommandApiService<AddProjectMemberRequest, AddProjectMemberResponse> {

    private final IProjectRepository projectRepository;
    private final EventPublisher eventPublisher;

    @Override
    public AddProjectMemberResponse execCommand(AddProjectMemberRequest req, JWTModel currentUser, String... args)
            throws Exception {
        if (req.getProjectId() == null) {
            throw new IllegalArgumentException("Project ID is required");
        }

        Project project = projectRepository.findById(new ProjectId(req.getProjectId()))
                .orElseThrow(() -> new DomainException("Project not found: " + req.getProjectId()));

        project.addMember(req.getEmployeeId(), req.getRole(), req.getAllocatedHours());

        projectRepository.save(project);

        eventPublisher.publishAll(project.getDomainEvents());
        project.clearDomainEvents();

        return new AddProjectMemberResponse(true);
    }
}
