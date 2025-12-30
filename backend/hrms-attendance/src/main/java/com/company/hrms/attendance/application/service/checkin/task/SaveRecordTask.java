package com.company.hrms.attendance.application.service.checkin.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.checkin.context.AttendanceContext;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveRecordTask implements PipelineTask<AttendanceContext> {

    private final IAttendanceRecordRepository attendanceRecordRepository;

    @Override
    public void execute(AttendanceContext context) throws Exception {
        var record = context.getRecord();
        log.debug("儲存打卡記錄: recordId={}", record.getId().getValue());

        attendanceRecordRepository.save(record);

        log.info("打卡記錄儲存成功: recordId={}", record.getId().getValue());
    }

    @Override
    public String getName() {
        return "儲存打卡記錄";
    }

    @Override
    public boolean shouldExecute(AttendanceContext context) {
        return context.getRecord() != null;
    }
}
