package com.company.hrms.project.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetMyProjectsRequest;
import com.company.hrms.project.api.response.GetMyProjectsResponse;
import com.company.hrms.project.api.response.GetMyProjectsResponse.MyProjectItemDto;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * 我的專案查詢服務 (ESS)
 * 查詢當前使用者參與的專案列表
 */
@Service("getMyProjectsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyProjectsServiceImpl implements QueryApiService<GetMyProjectsRequest, GetMyProjectsResponse> {

    private final IProjectRepository projectRepository;

    @Override
    public GetMyProjectsResponse getResponse(GetMyProjectsRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // 取得當前使用者 ID
        UUID employeeId = UUID.fromString(currentUser.getUserId());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 10);

        // 查詢使用者參與的專案 (透過 ProjectMember)
        Page<Project> pageResult = projectRepository.findByMemberEmployeeId(employeeId, pageable);

        List<MyProjectItemDto> items = pageResult.getContent().stream()
                .map(project -> toDto(project, employeeId))
                .collect(Collectors.toList());

        return GetMyProjectsResponse.builder()
                .items(items)
                .total(pageResult.getTotalElements())
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    private MyProjectItemDto toDto(Project project, UUID employeeId) {
        // 找出使用者在專案中的角色
        String role = project.getMembers().stream()
                .filter(m -> employeeId.equals(m.getEmployeeId()))
                .findFirst()
                .map(m -> m.getRole())
                .orElse("MEMBER");

        return MyProjectItemDto.builder()
                .projectId(project.getId().getValue())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .status(project.getStatus().name())
                .role(role)
                .startDate(project.getSchedule() != null ? project.getSchedule().getPlannedStartDate() : null)
                .endDate(project.getSchedule() != null ? project.getSchedule().getPlannedEndDate() : null)
                .budget(project.getBudget() != null ? project.getBudget().getBudgetAmount() : null)
                .build();
    }
}
