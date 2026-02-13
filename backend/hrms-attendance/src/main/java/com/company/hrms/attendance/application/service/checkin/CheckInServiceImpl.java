package com.company.hrms.attendance.application.service.checkin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.attendance.CheckInRequest;
import com.company.hrms.attendance.api.response.attendance.CheckInResponse;
import com.company.hrms.attendance.application.service.checkin.context.AttendanceContext;
import com.company.hrms.attendance.application.service.checkin.task.CreateCheckInRecordTask;
import com.company.hrms.attendance.application.service.checkin.task.SaveRecordTask;
import com.company.hrms.attendance.application.service.checkin.task.ValidateCheckInTask;
import com.company.hrms.attendance.domain.event.AttendanceRecordedEvent;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 上班打卡服務實作 (Pipeline 模式)
 * 
 * Pipeline 步驟：
 * 1. ValidateCheckInTask - 驗證打卡條件
 * 2. CreateCheckInRecordTask - 建立打卡記錄
 * 3. SaveRecordTask - 儲存記錄
 */
@Service("checkInServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CheckInServiceImpl implements CommandApiService<CheckInRequest, CheckInResponse> {

    private final ValidateCheckInTask validateCheckInTask;
    private final CreateCheckInRecordTask createCheckInRecordTask;
    private final SaveRecordTask saveRecordTask;
    private final EventPublisher eventPublisher;

    @Override
    public CheckInResponse execCommand(CheckInRequest request, JWTModel currentUser, String... args) throws Exception {
        log.info("上班打卡流程開始: employeeId={}", request.getEmployeeId());

        AttendanceContext context = new AttendanceContext(request, currentUser.getTenantId());

        BusinessPipeline.start(context)
                .next(validateCheckInTask)
                .next(createCheckInRecordTask)
                .next(saveRecordTask)
                .execute();

        var record = context.getRecord();
        var shift = context.getShift();

        eventPublisher.publish(new AttendanceRecordedEvent(
                record.getId().getValue(),
                record.getEmployeeId(),
                record.getDate(),
                record.getCheckInTime(),
                record.getCheckOutTime(),
                record.isLate(),
                record.isEarlyLeave()));

        log.info("上班打卡流程完成: recordId={}", record.getId().getValue());

        return CheckInResponse.success(
                record.getId().getValue(),
                record.getCheckInTime(),
                record.isLate(),
                record.getLateMinutes(),
                shift.getName());
    }
}
