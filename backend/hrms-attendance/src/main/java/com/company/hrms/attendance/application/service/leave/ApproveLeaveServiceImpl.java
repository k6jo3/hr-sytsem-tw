package com.company.hrms.attendance.application.service.leave;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.leave.ApproveLeaveRequest;
import com.company.hrms.attendance.api.response.leave.ApproveLeaveResponse;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 請假審核服務實作
 */
@Service("approveLeaveServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApproveLeaveServiceImpl implements CommandApiService<ApproveLeaveRequest, ApproveLeaveResponse> {

    private final ILeaveApplicationRepository leaveApplicationRepository;

    @Override
    public ApproveLeaveResponse execCommand(ApproveLeaveRequest request, JWTModel currentUser, String... args)
            throws Exception {
        log.info("請假審核流程開始: applicationId={}", request.getApplicationId());

        LeaveApplication application = leaveApplicationRepository
                .findById(new ApplicationId(request.getApplicationId()))
                .orElseThrow(() -> new IllegalArgumentException("找不到請假申請: " + request.getApplicationId()));

        application.approve();
        leaveApplicationRepository.save(application);

        log.info("請假審核流程完成: applicationId={}, status=APPROVED", request.getApplicationId());

        return ApproveLeaveResponse.approved(request.getApplicationId());
    }
}
