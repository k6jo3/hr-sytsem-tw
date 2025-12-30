package com.company.hrms.attendance.application.service.checkout.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.checkout.context.CheckOutContext;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存打卡記錄 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveCheckOutRecordTask implements PipelineTask<CheckOutContext> {

    private final IAttendanceRecordRepository attendanceRecordRepository;

    @Override
    public void execute(CheckOutContext context) throws Exception {
        var record = context.getRecord();
        log.debug("儲存下班打卡記錄: recordId={}", record.getId().getValue());

        attendanceRecordRepository.save(record);

        log.info("下班打卡記錄儲存成功: recordId={}", record.getId().getValue());
    }

    @Override
    public String getName() {
        return "儲存下班打卡記錄";
    }

    @Override
    public boolean shouldExecute(CheckOutContext context) {
        return context.getRecord() != null;
    }
}
