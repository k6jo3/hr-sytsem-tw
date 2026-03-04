package com.company.hrms.project.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetProjectMemberListRequest;
import com.company.hrms.project.api.response.ProjectMemberDto;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.aggregate.ProjectMember;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢專案成員列表 Service
 */
@Service("getProjectMembersServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProjectMembersServiceImpl
        implements QueryApiService<GetProjectMemberListRequest, List<ProjectMemberDto>> {

    private final IProjectRepository projectRepository;

    @Override
    public List<ProjectMemberDto> getResponse(GetProjectMemberListRequest req, JWTModel currentUser, String... args)
            throws Exception {
        Project project = projectRepository.findById(new ProjectId(req.getProjectId()))
                .orElseThrow(() -> new DomainException("專案不存在: " + req.getProjectId()));

        return project.getMembers().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ProjectMemberDto toDto(ProjectMember member) {
        return ProjectMemberDto.builder()
                .id(member.getId().toString())
                .employeeId(member.getEmployeeId())
                .role(member.getRole())
                .allocatedHours(member.getAllocatedHours())
                .joinDate(member.getJoinDate())
                .leaveDate(member.getLeaveDate())
                .build();
    }
}
