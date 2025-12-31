package com.company.hrms.attendance.application.service.correction.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.correction.context.CorrectionContext;
import com.company.hrms.attendance.domain.repository.ICorrectionRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存補卡申請 Task
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaveCorrectionTask implements PipelineTask<CorrectionContext> {

    private final ICorrectionRepository correctionRepository;

    @Override
    public void execute(CorrectionContext context) throws Exception {
        var application = context.getApplication();

        correctionRepository.save(application);

        log.info("補卡申請儲存成功: applicationId={}", application.getId().getValue());
    }

    @Override
    public String getName() {
        return "儲存補卡申請";
    }

    @Override
    public boolean shouldExecute(CorrectionContext context) {
        return context.getApplication() != null;
    }
}
