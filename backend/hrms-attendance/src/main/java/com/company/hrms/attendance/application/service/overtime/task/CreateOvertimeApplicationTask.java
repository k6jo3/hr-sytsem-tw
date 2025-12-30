package com.company.hrms.attendance.application.service.overtime.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.overtime.context.OvertimeContext;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeType;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立加班申請 Task
 * - 建立 OvertimeApplication 聚合根
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateOvertimeApplicationTask implements PipelineTask<OvertimeContext> {

    @Override
    public void execute(OvertimeContext context) throws Exception {
        var request = context.getApplyRequest();

        log.debug("建立加班申請: employeeId={}, date={}, hours={}",
                request.getEmployeeId(), request.getDate(), request.getHours());

        OvertimeApplication application = new OvertimeApplication(
                new OvertimeId(java.util.UUID.randomUUID().toString()),
                request.getEmployeeId(),
                request.getDate(),
                request.getHours(),
                OvertimeType.valueOf(request.getOvertimeType()),
                request.getReason());

        context.setApplication(application);
        log.info("加班申請建立成功: applicationId={}", application.getId().getValue());
    }

    @Override
    public String getName() {
        return "建立加班申請";
    }

    @Override
    public boolean shouldExecute(OvertimeContext context) {
        return context.getApplyRequest() != null;
    }
}
