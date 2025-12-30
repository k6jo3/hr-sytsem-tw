package com.company.hrms.attendance.application.service.leave;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.leave.ApplyLeaveRequest;
import com.company.hrms.attendance.api.response.leave.ApplyLeaveResponse;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 請假申請服務實作
 */
@Service("applyLeaveServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyLeaveServiceImpl implements CommandApiService<ApplyLeaveRequest, ApplyLeaveResponse> {

    private final ILeaveApplicationRepository leaveApplicationRepository;

    @Override
    public ApplyLeaveResponse execCommand(ApplyLeaveRequest request, JWTModel currentUser, String... args)
            throws Exception {
        log.info("請假申請流程開始: employeeId={}, leaveType={}",
                request.getEmployeeId(), request.getLeaveTypeId());

        // Create LeaveApplication
        LeaveApplication application = new LeaveApplication(
                new ApplicationId(java.util.UUID.randomUUID().toString()),
                request.getEmployeeId(),
                new LeaveTypeId(request.getLeaveTypeId()),
                request.getStartDate(),
                request.getEndDate(),
                request.getStartPeriod() != null
                        ? LeavePeriodType.valueOf(request.getStartPeriod())
                        : LeavePeriodType.FULL_DAY,
                request.getEndPeriod() != null
                        ? LeavePeriodType.valueOf(request.getEndPeriod())
                        : LeavePeriodType.FULL_DAY,
                request.getReason());

        if (request.getProofAttachmentUrl() != null) {
            application.setProofAttachmentUrl(request.getProofAttachmentUrl());
        }

        leaveApplicationRepository.save(application);

        log.info("請假申請流程完成: applicationId={}", application.getId().getValue());

        return ApplyLeaveResponse.success(application.getId().getValue());
    }
}
