package com.company.hrms.attendance.application.service.leave;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.leave.ApplyLeaveRequest;
import com.company.hrms.attendance.api.response.leave.ApplyLeaveResponse;
import com.company.hrms.attendance.application.service.leave.context.LeaveContext;
import com.company.hrms.attendance.application.service.leave.task.CreateLeaveApplicationTask;
import com.company.hrms.attendance.application.service.leave.task.SaveLeaveApplicationTask;
import com.company.hrms.attendance.domain.event.LeaveAppliedEvent;
import com.company.hrms.attendance.domain.service.LeaveCalculationDomainService;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 請假申請服務實作 (Pipeline 模式)
 * 
 * <p>
 * Pipeline 步驟：
 * <ol>
 * <li>CreateLeaveApplicationTask - 建立請假申請聚合根</li>
 * <li>SaveLeaveApplicationTask - 儲存請假申請</li>
 * </ol>
 * 
 * <p>
 * 注意：此服務未包含驗證 Task，因為 LeaveApplication 聚合根建構子
 * 已內建日期驗證邏輯。若需要更複雜的驗證（如假期餘額檢查），
 * 可新增 ValidateLeaveApplicationTask。
 */
@Service("applyLeaveServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyLeaveServiceImpl implements CommandApiService<ApplyLeaveRequest, ApplyLeaveResponse> {

        private final CreateLeaveApplicationTask createLeaveApplicationTask;
        private final SaveLeaveApplicationTask saveLeaveApplicationTask;
        private final EventPublisher eventPublisher;
        private final LeaveCalculationDomainService leaveCalculationDomainService;

        @Override
        public ApplyLeaveResponse execCommand(ApplyLeaveRequest request, JWTModel currentUser, String... args)
                        throws Exception {
                log.info("請假申請流程開始: employeeId={}, leaveType={}",
                                request.getEmployeeId(), request.getLeaveTypeId());

                LeaveContext context = new LeaveContext(request, currentUser.getTenantId());

                BusinessPipeline.start(context)
                                .next(createLeaveApplicationTask)
                                .next(saveLeaveApplicationTask)
                                .execute();

                // 計算天數並發布領域事件
                BigDecimal totalDays = leaveCalculationDomainService.calculateTotalDays(
                                request.getStartDate(), request.getEndDate());

                eventPublisher.publish(new LeaveAppliedEvent(
                                context.getApplication().getId().getValue(),
                                request.getEmployeeId(),
                                request.getLeaveTypeId(),
                                totalDays));

                log.info("請假申請流程完成: applicationId={}", context.getApplication().getId().getValue());

                return ApplyLeaveResponse.success(context.getApplication().getId().getValue());
        }
}
