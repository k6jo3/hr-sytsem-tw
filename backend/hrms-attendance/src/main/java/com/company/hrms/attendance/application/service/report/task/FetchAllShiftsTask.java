package com.company.hrms.attendance.application.service.report.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.report.context.DailyReportContext;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;

/**
 * 獲取所有班別 Task
 */
@Component
@RequiredArgsConstructor
public class FetchAllShiftsTask implements PipelineTask<DailyReportContext> {

    private final IShiftRepository shiftRepository;

    @Override
    public void execute(DailyReportContext context) throws Exception {
        // 簡單起見獲取所有班別，日報彙總需要班別資訊來判定遲到/早退/應出勤
        List<Shift> shifts = shiftRepository.findAll();
        context.setAllShifts(shifts);
    }
}
