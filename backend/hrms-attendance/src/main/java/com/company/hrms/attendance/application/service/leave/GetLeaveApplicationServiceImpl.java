package com.company.hrms.attendance.application.service.leave;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.controller.leave.HR03LeaveQryController;
import com.company.hrms.attendance.api.response.leave.LeaveApplicationDetailResponse;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢請假申請詳情服務實作
 */
@Service("getLeaveApplicationServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetLeaveApplicationServiceImpl
        implements QueryApiService<HR03LeaveQryController.GetLeaveApplicationRequest, LeaveApplicationDetailResponse> {

    private final ILeaveApplicationRepository leaveApplicationRepository;

    @Override
    public LeaveApplicationDetailResponse getResponse(HR03LeaveQryController.GetLeaveApplicationRequest request,
            JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Application ID is required");
        }
        String applicationId = args[0];
        log.info("查詢請假申請詳情: applicationId={}", applicationId);

        LeaveApplication entity = leaveApplicationRepository.findById(new ApplicationId(applicationId))
                .orElseThrow(() -> new EntityNotFoundException("請假申請不存在: " + applicationId));

        return toResponse(entity);
    }

    private LeaveApplicationDetailResponse toResponse(LeaveApplication entity) {
        BigDecimal days = BigDecimal.ZERO;
        if (entity.getStartDate() != null && entity.getEndDate() != null) {
            long daysDiff = ChronoUnit.DAYS.between(entity.getStartDate(), entity.getEndDate()) + 1;
            days = BigDecimal.valueOf(daysDiff);
        }

        return LeaveApplicationDetailResponse.builder()
                .applicationId(entity.getId().getValue())
                .employeeId(entity.getEmployeeId())
                // .employeeName("TODO")
                .leaveTypeCode(entity.getLeaveTypeId().getValue())
                .leaveTypeName(entity.getLeaveTypeId().getValue())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .startHalfDay(entity.getStartPeriod() != null ? entity.getStartPeriod().name() : null)
                .endHalfDay(entity.getEndPeriod() != null ? entity.getEndPeriod().name() : null)
                .leaveDays(days)
                .reason(entity.getReason())
                .status(entity.getStatus().name())
                .attachments(entity.getProofAttachmentUrl() != null
                        ? java.util.Collections.singletonList(entity.getProofAttachmentUrl())
                        : java.util.Collections.emptyList())
                .build();
    }
}
