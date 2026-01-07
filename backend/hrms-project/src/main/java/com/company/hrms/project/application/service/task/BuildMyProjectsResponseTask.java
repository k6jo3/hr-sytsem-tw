package com.company.hrms.project.application.service.task;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.project.api.response.GetMyProjectsResponse;
import com.company.hrms.project.api.response.GetMyProjectsResponse.MyProjectItemDto;
import com.company.hrms.project.application.service.context.MyProjectsContext;
import com.company.hrms.project.domain.model.aggregate.Project;

/**
 * 建構我的專案回應 Task
 * 
 * Domain Task: 負責將 Domain Model 轉換為 Response DTO
 */
@Component
public class BuildMyProjectsResponseTask implements PipelineTask<MyProjectsContext> {

        @Override
        public void execute(MyProjectsContext context) throws Exception {
                // 轉換為 DTO 列表
                List<MyProjectItemDto> items = context.getProjects().getContent().stream()
                                .map(project -> toDto(project, context.getEmployeeId()))
                                .collect(Collectors.toList());

                context.setProjectItems(items);

                // 建構最終回應
                GetMyProjectsResponse response = GetMyProjectsResponse.builder()
                                .items(items)
                                .total(context.getProjects().getTotalElements())
                                .page(context.getProjects().getNumber())
                                .size(context.getProjects().getSize())
                                .totalPages(context.getProjects().getTotalPages())
                                .build();

                context.setResponse(response);
        }

        /**
         * 轉換 Project 為 MyProjectItemDto
         */
        private MyProjectItemDto toDto(Project project, java.util.UUID employeeId) {
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
                                .startDate(project.getSchedule() != null ? project.getSchedule().getPlannedStartDate()
                                                : null)
                                .endDate(project.getSchedule() != null ? project.getSchedule().getPlannedEndDate()
                                                : null)
                                .budget(project.getBudget() != null ? project.getBudget().getBudgetAmount() : null)
                                .build();
        }
}
