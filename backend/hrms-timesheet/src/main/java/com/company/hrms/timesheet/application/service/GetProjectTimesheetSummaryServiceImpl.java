package com.company.hrms.timesheet.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.timesheet.api.request.GetProjectTimesheetSummaryRequest;
import com.company.hrms.timesheet.api.response.GetProjectTimesheetSummaryResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Service("getProjectTimesheetSummaryServiceImpl")
@RequiredArgsConstructor
public class GetProjectTimesheetSummaryServiceImpl
        implements QueryApiService<GetProjectTimesheetSummaryRequest, GetProjectTimesheetSummaryResponse> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public GetProjectTimesheetSummaryResponse getResponse(GetProjectTimesheetSummaryRequest request,
            JWTModel currentUser, String... args)
            throws Exception {

        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // 查詢所有符合條件的工時表（不分頁）
        var timesheets = timesheetRepository.findAll(query, org.springframework.data.domain.Pageable.unpaged())
                .getContent();

        // 按專案彙總
        Map<UUID, BigDecimal> projectHoursMap = new HashMap<>();
        Map<UUID, Set<UUID>> projectEmployeesMap = new HashMap<>();

        for (Timesheet timesheet : timesheets) {
            for (var entry : timesheet.getEntries()) {
                UUID projectId = entry.getProjectId();

                // 如果有指定專案篩選
                if (request.getProjectId() != null && !projectId.equals(request.getProjectId())) {
                    continue;
                }

                // 累計工時
                projectHoursMap.merge(projectId, entry.getHours(), BigDecimal::add);

                // 記錄員工
                projectEmployeesMap.computeIfAbsent(projectId, k -> new HashSet<>())
                        .add(timesheet.getEmployeeId());
            }
        }

        // 建立回應
        List<GetProjectTimesheetSummaryResponse.ProjectSummary> summaries = new ArrayList<>();
        for (Map.Entry<UUID, BigDecimal> entry : projectHoursMap.entrySet()) {
            summaries.add(GetProjectTimesheetSummaryResponse.ProjectSummary.builder()
                    .projectId(entry.getKey())
                    .projectName("Project-" + entry.getKey().toString().substring(0, 8)) // 簡化實作
                    .totalHours(entry.getValue())
                    .employeeCount(projectEmployeesMap.get(entry.getKey()).size())
                    .build());
        }

        return GetProjectTimesheetSummaryResponse.builder()
                .projects(summaries)
                .build();
    }
}
