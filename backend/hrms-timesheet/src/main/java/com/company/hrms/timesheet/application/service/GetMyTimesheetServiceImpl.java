package com.company.hrms.timesheet.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.timesheet.api.request.GetMyTimesheetRequest;
import com.company.hrms.timesheet.api.response.GetMyTimesheetResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Service("getMyTimesheetServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyTimesheetServiceImpl implements QueryApiService<GetMyTimesheetRequest, GetMyTimesheetResponse> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public GetMyTimesheetResponse getResponse(GetMyTimesheetRequest request, JWTModel currentUser, String... args)
            throws Exception {

        // 確保 Employee ID 為當前使用者
        UUID employeeId = UUID.fromString(currentUser.getUserId());
        request.setEmployeeId(employeeId);

        // 建構查詢條件
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // 分頁處理
        PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getSize(),
                Sort.by(Sort.Direction.DESC, "periodStartDate"));

        Page<Timesheet> page = timesheetRepository.findAll(query, pageRequest);

        // 轉換為 DTO

        List<GetMyTimesheetResponse.TimesheetSummaryDto> items = page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return GetMyTimesheetResponse.builder()
                .items(items)
                .total(page.getTotalElements())
                .build();
    }

    private GetMyTimesheetResponse.TimesheetSummaryDto toDto(Timesheet t) {
        return GetMyTimesheetResponse.TimesheetSummaryDto.builder()
                .timesheetId(t.getId().toString())
                .periodStartDate(t.getPeriodStartDate().toString())
                .periodEndDate(t.getPeriodEndDate().toString())
                .totalHours(t.getTotalHours())
                .status(t.getStatus())
                .submittedAt(t.getSubmittedAt() != null ? t.getSubmittedAt().toString() : null)
                .approvedAt(t.getApprovedAt() != null ? t.getApprovedAt().toString() : null)
                .build();
    }
}
