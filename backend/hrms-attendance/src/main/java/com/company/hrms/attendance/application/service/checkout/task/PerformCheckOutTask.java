package com.company.hrms.attendance.application.service.checkout.task;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.checkout.context.CheckOutContext;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行下班打卡 Task
 * - 取得班別設定
 * - 執行打卡並計算是否早退
 * - 計算工時
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformCheckOutTask implements PipelineTask<CheckOutContext> {

    private final IShiftRepository shiftRepository;

    @Override
    public void execute(CheckOutContext context) throws Exception {
        var request = context.getCheckOutRequest();
        var record = context.getRecord();
        LocalDateTime checkOutTime = request.getCheckOutTime() != null
                ? request.getCheckOutTime()
                : LocalDateTime.now();

        log.debug("執行下班打卡: recordId={}", record.getId().getValue());

        // Get shift
        Shift shift = shiftRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到班別設定"));

        // Perform check-out
        record.checkOut(checkOutTime, shift);

        // Calculate working hours
        double workingHours = Duration.between(record.getCheckInTime(), checkOutTime).toMinutes() / 60.0;

        context.setShift(shift);
        context.setWorkingHours(workingHours);

        log.info("下班打卡執行完成: recordId={}, isEarlyLeave={}, workingHours={}",
                record.getId().getValue(), record.isEarlyLeave(), workingHours);
    }

    @Override
    public String getName() {
        return "執行下班打卡";
    }

    @Override
    public boolean shouldExecute(CheckOutContext context) {
        return context.getRecord() != null;
    }
}
