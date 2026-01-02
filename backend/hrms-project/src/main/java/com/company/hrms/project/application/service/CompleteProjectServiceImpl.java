package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.CompleteProjectRequest;
import com.company.hrms.project.api.response.CompleteProjectResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

@Service("completeProjectServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CompleteProjectServiceImpl implements CommandApiService<CompleteProjectRequest, CompleteProjectResponse> {

    private final IProjectRepository projectRepository;

    @Override
    public CompleteProjectResponse execCommand(CompleteProjectRequest req, JWTModel currentUser, String... args)
            throws Exception {
        if (req.getProjectId() == null) {
            throw new IllegalArgumentException("Project ID is required");
        }

        Project project = projectRepository.findById(new ProjectId(req.getProjectId()))
                .orElseThrow(() -> new DomainException("Project not found: " + req.getProjectId()));

        project.complete();

        projectRepository.save(project);

        return new CompleteProjectResponse(true, project.getStatus().name());
    }
}
