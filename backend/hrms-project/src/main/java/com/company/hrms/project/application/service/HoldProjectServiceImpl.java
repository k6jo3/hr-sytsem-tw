package com.company.hrms.project.application.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.HoldProjectRequest;
import com.company.hrms.project.api.response.HoldProjectResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * 暫停專案服務
 */
@Service("holdProjectServiceImpl")
@RequiredArgsConstructor
@Transactional
public class HoldProjectServiceImpl implements CommandApiService<HoldProjectRequest, HoldProjectResponse> {

    private final IProjectRepository projectRepository;

    @Override
    public HoldProjectResponse execCommand(HoldProjectRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 載入專案
        Project project = projectRepository.findById(new ProjectId(req.getProjectId()))
                .orElseThrow(() -> new DomainException("專案不存在: " + req.getProjectId()));

        // 2. 暫停專案
        project.hold(req.getReason());

        // 3. 儲存
        projectRepository.save(project);

        return HoldProjectResponse.builder()
                .projectId(project.getId().getValue().toString())
                .status(project.getStatus())
                .holdReason(req.getReason())
                .holdDate(LocalDate.now())
                .build();
    }
}
