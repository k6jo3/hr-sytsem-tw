package com.company.hrms.attendance.application.service.correction.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.correction.context.ApproveCorrectionContext;
import com.company.hrms.attendance.domain.repository.ICorrectionRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存補卡核准結果 Task
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaveApproveCorrectionTask implements PipelineTask<ApproveCorrectionContext> {

    private final ICorrectionRepository correctionRepository;

    @Override
    public void execute(ApproveCorrectionContext context) throws Exception {
        var application = context.getApplication();

        correctionRepository.save(application);

        log.info("補卡核准儲存成功: applicationId={}", application.getId().getValue());
    }

    @Override
    public String getName() {
        return "儲存補卡核准結果";
    }

    @Override
    public boolean shouldExecute(ApproveCorrectionContext context) {
        return context.getApplication() != null;
    }
}
