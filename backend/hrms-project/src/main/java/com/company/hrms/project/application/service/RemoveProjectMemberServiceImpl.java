package com.company.hrms.project.application.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.RemoveProjectMemberRequest;
import com.company.hrms.project.api.response.RemoveProjectMemberResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * 移除專案成員服務
 */
@Service("removeProjectMemberServiceImpl")
@RequiredArgsConstructor
@Transactional
public class RemoveProjectMemberServiceImpl
        implements CommandApiService<RemoveProjectMemberRequest, RemoveProjectMemberResponse> {

    private final IProjectRepository projectRepository;

    @Override
    public RemoveProjectMemberResponse execCommand(RemoveProjectMemberRequest req, JWTModel currentUser,
            String... args) throws Exception {

        // 1. 載入專案
        Project project = projectRepository.findById(new ProjectId(req.getProjectId()))
                .orElseThrow(() -> new DomainException("專案不存在: " + req.getProjectId()));

        // 2. 移除成員
        UUID memberId = UUID.fromString(req.getMemberId());
        LocalDate leaveDate = LocalDate.now();
        project.removeMember(memberId, leaveDate);

        // 3. 儲存
        projectRepository.save(project);

        return RemoveProjectMemberResponse.builder()
                .memberId(req.getMemberId())
                .removed(true)
                .leaveDate(leaveDate)
                .build();
    }
}
