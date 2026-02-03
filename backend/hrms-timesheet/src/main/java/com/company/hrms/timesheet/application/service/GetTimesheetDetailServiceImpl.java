package com.company.hrms.timesheet.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
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

                // 權限檢查
                validatePermission(t, currentUser);

                return toResponse(t);
        }

        /**
         * 驗證存取權限
         * 1. 只有本人可以查看自己的工時表
         * 2. 具有管理權限 (timesheet:approve 或 timesheet:read:all) 的使用者可以查看
         * 3. 管理員 (ADMIN) 可以查看
         */
        private void validatePermission(Timesheet t, JWTModel currentUser) {
                UUID currentUserId = UUID.fromString(currentUser.getUserId());
                boolean isOwner = t.getEmployeeId().equals(currentUserId);
                boolean isAdmin = currentUser.hasRole("ADMIN");
                boolean hasReadPermission = currentUser.hasPermission("timesheet:approve") ||
                                currentUser.hasPermission("timesheet:read:all");

                if (!isOwner && !isAdmin && !hasReadPermission) {
                        throw new DomainException("您無權限查看此工時表詳情");
                }
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
