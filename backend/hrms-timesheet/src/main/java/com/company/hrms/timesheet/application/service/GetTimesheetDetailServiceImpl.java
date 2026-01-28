package com.company.hrms.timesheet.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.timesheet.api.request.GetTimesheetDetailRequest;
import com.company.hrms.timesheet.api.response.GetTimesheetDetailResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;
import com.company.hrms.common.exception.DomainException;

@Service("getTimesheetDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTimesheetDetailServiceImpl
                implements QueryApiService<GetTimesheetDetailRequest, GetTimesheetDetailResponse> {

        private final ITimesheetRepository timesheetRepository;

        @Override
        public GetTimesheetDetailResponse getResponse(GetTimesheetDetailRequest request, JWTModel currentUser,
                        String... args)
                        throws Exception {

                Timesheet t = timesheetRepository.findById(new TimesheetId(UUID.fromString(request.getTimesheetId())))
                                .orElseThrow(() -> new EntityNotFoundException("Timesheet", request.getTimesheetId()));

                // 基本權限檢查 (可選：移至 Policy)
                // 僅擁有者或審核者 (若已分配) 可查看詳情
                // UUID userId = UUID.fromString(currentUser.getUserId());
                // boolean isOwner = t.getEmployeeId().equals(userId);
                // boolean isApprover = t.getApprovedBy() != null &&
                // t.getApprovedBy().equals(userId);
                // if (!isOwner && !isApprover) {
                // 目前假設若持有 ID 即可查看 (或依賴 Gateway/Role)
                // 或在此實作嚴格檢查
                // throw new DomainException("無權限查看此工時表");
                // }

                return toResponse(t);
        }

        private GetTimesheetDetailResponse toResponse(Timesheet t) {
                List<GetTimesheetDetailResponse.TimesheetEntryDto> entryDtos = t.getEntries().stream()
                                .map(this::toEntryDto)
                                .collect(Collectors.toList());

                return GetTimesheetDetailResponse.builder()
                                .timesheetId(t.getId().getValue())
                                .employeeId(t.getEmployeeId())
                                .periodStartDate(t.getPeriodStartDate())
                                .periodEndDate(t.getPeriodEndDate())
                                .totalHours(t.getTotalHours())
                                .status(t.getStatus())
                                .submittedAt(t.getSubmittedAt())
                                .approvedBy(t.getApprovedBy())
                                .approvedAt(t.getApprovedAt())
                                .rejectionReason(t.getRejectionReason())
                                .entries(entryDtos)
                                .build();
        }

        private GetTimesheetDetailResponse.TimesheetEntryDto toEntryDto(TimesheetEntry e) {
                return GetTimesheetDetailResponse.TimesheetEntryDto.builder()
                                .entryId(e.getId())
                                .workDate(e.getWorkDate())
                                .hours(e.getHours())
                                .projectId(e.getProjectId())
                                .description(e.getDescription())
                                // .location(e.getLocation()) // Not in domain model
                                .build();
        }
}
