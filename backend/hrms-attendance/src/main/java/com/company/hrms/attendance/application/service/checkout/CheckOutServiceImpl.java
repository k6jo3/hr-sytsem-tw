package com.company.hrms.attendance.application.service.checkout;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.attendance.CheckOutRequest;
import com.company.hrms.attendance.api.response.attendance.CheckOutResponse;
import com.company.hrms.attendance.application.service.checkout.context.CheckOutContext;
import com.company.hrms.attendance.application.service.checkout.task.PerformCheckOutTask;
import com.company.hrms.attendance.application.service.checkout.task.SaveCheckOutRecordTask;
import com.company.hrms.attendance.application.service.checkout.task.ValidateCheckOutTask;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 下班打卡服務實作 (Pipeline 模式)
 * 
 * <p>
 * Pipeline 步驟：
 * <ol>
 * <li>ValidateCheckOutTask - 驗證打卡條件（是否已上班打卡、是否已下班打卡）</li>
 * <li>PerformCheckOutTask - 執行打卡並計算早退/工時</li>
 * <li>SaveCheckOutRecordTask - 儲存打卡記錄</li>
 * </ol>
 */
@Service("checkOutServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CheckOutServiceImpl implements CommandApiService<CheckOutRequest, CheckOutResponse> {

    private final ValidateCheckOutTask validateCheckOutTask;
    private final PerformCheckOutTask performCheckOutTask;
    private final SaveCheckOutRecordTask saveCheckOutRecordTask;

    @Override
    public CheckOutResponse execCommand(CheckOutRequest request, JWTModel currentUser, String... args)
            throws Exception {
        log.info("下班打卡流程開始: employeeId={}", request.getEmployeeId());

        CheckOutContext context = new CheckOutContext(request, currentUser.getTenantId());

        BusinessPipeline.start(context)
                .next(validateCheckOutTask)
                .next(performCheckOutTask)
                .next(saveCheckOutRecordTask)
                .execute();

        var record = context.getRecord();

        log.info("下班打卡流程完成: recordId={}", record.getId().getValue());

        return CheckOutResponse.success(
                record.getId().getValue(),
                record.getCheckInTime(),
                record.getCheckOutTime(),
                context.getWorkingHours(),
                record.isEarlyLeave(),
                record.getEarlyLeaveMinutes());
    }
}
