package com.company.hrms.attendance.application.service.overtime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.overtime.ApplyOvertimeRequest;
import com.company.hrms.attendance.api.response.overtime.ApplyOvertimeResponse;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeType;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 加班申請服務實作
 */
@Service("applyOvertimeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyOvertimeServiceImpl implements CommandApiService<ApplyOvertimeRequest, ApplyOvertimeResponse> {

    private final IOvertimeApplicationRepository overtimeApplicationRepository;

    @Override
    public ApplyOvertimeResponse execCommand(ApplyOvertimeRequest request, JWTModel currentUser, String... args)
            throws Exception {
        log.info("加班申請流程開始: employeeId={}, date={}, hours={}",
                request.getEmployeeId(), request.getDate(), request.getHours());

        OvertimeApplication application = new OvertimeApplication(
                new OvertimeId(java.util.UUID.randomUUID().toString()),
                request.getEmployeeId(),
                request.getDate(),
                request.getHours(),
                OvertimeType.valueOf(request.getOvertimeType()),
                request.getReason());

        overtimeApplicationRepository.save(application);

        log.info("加班申請流程完成: applicationId={}", application.getId().getValue());

        return ApplyOvertimeResponse.success(application.getId().getValue());
    }
}
