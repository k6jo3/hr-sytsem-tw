package com.company.hrms.attendance.application.service.correction.task;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.correction.context.ApproveCorrectionContext;
import com.company.hrms.attendance.domain.model.aggregate.CorrectionApplication;
import com.company.hrms.attendance.domain.repository.ICorrectionRepository;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 載入補卡申請 Task
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoadCorrectionTask implements PipelineTask<ApproveCorrectionContext> {

    private final ICorrectionRepository correctionRepository;

    @Override
    public void execute(ApproveCorrectionContext context) throws Exception {
        String correctionId = context.getCorrectionId();

        CorrectionApplication application = correctionRepository.findById(correctionId)
                .orElseThrow(() -> new EntityNotFoundException("CorrectionApplication", correctionId));

        context.setApplication(application);
        log.info("補卡申請載入成功: applicationId={}, status={}", correctionId, application.getStatus());
    }

    @Override
    public String getName() {
        return "載入補卡申請";
    }

    @Override
    public boolean shouldExecute(ApproveCorrectionContext context) {
        return context.getCorrectionId() != null;
    }
}
