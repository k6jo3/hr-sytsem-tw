package com.company.hrms.attendance.application.service.correction.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.correction.context.CorrectionContext;
import com.company.hrms.attendance.domain.model.aggregate.CorrectionApplication;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionId;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionType;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.extern.slf4j.Slf4j;

/**
 * 建立補卡申請 Task
 */
@Slf4j
@Component
public class CreateCorrectionTask implements PipelineTask<CorrectionContext> {

    @Override
    public void execute(CorrectionContext context) throws Exception {
        var request = context.getRequest();

        CorrectionApplication application = new CorrectionApplication(
                CorrectionId.next(),
                request.getEmployeeId(),
                request.getAttendanceRecordId(),
                request.getCorrectionDate(),
                CorrectionType.valueOf(request.getCorrectionType()),
                request.getCorrectedCheckInTime(),
                request.getCorrectedCheckOutTime(),
                request.getReason());

        context.setApplication(application);
        log.info("補卡申請建立成功: applicationId={}", application.getId().getValue());
    }

    @Override
    public String getName() {
        return "建立補卡申請";
    }

    @Override
    public boolean shouldExecute(CorrectionContext context) {
        return context.getRequest() != null;
    }
}
