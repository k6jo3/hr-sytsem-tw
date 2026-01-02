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
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.timesheet.api.request.GetPendingApprovalsRequest;
import com.company.hrms.timesheet.api.response.GetPendingApprovalsResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Service("getPendingApprovalsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPendingApprovalsServiceImpl
                implements QueryApiService<GetPendingApprovalsRequest, GetPendingApprovalsResponse> {

        private final ITimesheetRepository timesheetRepository;

        @Override
        public GetPendingApprovalsResponse getResponse(GetPendingApprovalsRequest request, JWTModel currentUser,
                        String... args)
                        throws Exception {

                UUID approverId = UUID.fromString(currentUser.getUserId());

                // 分頁處理
                PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getSize(),
                                Sort.by(Sort.Direction.ASC, "submittedAt"));

                // 使用自定義 Repository 方法
                Page<Timesheet> page = timesheetRepository.findPendingApprovals(approverId, pageRequest);

                List<GetPendingApprovalsResponse.TimesheetSummaryDto> items = page.getContent().stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());

                return GetPendingApprovalsResponse.builder()
                                .items(items)
                                .total(page.getTotalElements())
                                .build();
        }

        private GetPendingApprovalsResponse.TimesheetSummaryDto toDto(Timesheet t) {
                return GetPendingApprovalsResponse.TimesheetSummaryDto.builder()
                                .timesheetId(t.getId().toString())
                                .employeeId(t.getEmployeeId().toString())
                                .periodStartDate(t.getPeriodStartDate().toString())
                                .periodEndDate(t.getPeriodEndDate().toString())
                                .totalHours(t.getTotalHours())
                                .status(t.getStatus())
                                .submittedAt(t.getSubmittedAt() != null ? t.getSubmittedAt().toString() : null)
                                .build();
        }
}
