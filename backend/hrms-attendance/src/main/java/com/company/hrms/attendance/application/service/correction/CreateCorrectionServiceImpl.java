package com.company.hrms.attendance.application.service.correction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.attendance.CreateCorrectionRequest;
import com.company.hrms.attendance.api.response.attendance.CreateCorrectionResponse;
import com.company.hrms.attendance.application.service.correction.context.CorrectionContext;
import com.company.hrms.attendance.application.service.correction.task.CreateCorrectionTask;
import com.company.hrms.attendance.application.service.correction.task.SaveCorrectionTask;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 補卡申請提交服務
 */
@Slf4j
@Service("createCorrectionServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CreateCorrectionServiceImpl
        implements CommandApiService<CreateCorrectionRequest, CreateCorrectionResponse> {

    private final CreateCorrectionTask createCorrectionTask;
    private final SaveCorrectionTask saveCorrectionTask;

    @Override
    public CreateCorrectionResponse execCommand(CreateCorrectionRequest request, JWTModel currentUser, String... args)
            throws Exception {
        log.info("提交補卡申請流程開始: employeeId={}, correctionDate={}", request.getEmployeeId(), request.getCorrectionDate());

        CorrectionContext context = new CorrectionContext(request, currentUser.getTenantId());

        BusinessPipeline.start(context)
                .next(createCorrectionTask)
                .next(saveCorrectionTask)
                .execute();

        var application = context.getApplication();
        log.info("提交補卡申請流程結束: applicationId={}", application.getId().getValue());

        return CreateCorrectionResponse.builder()
                .correctionId(application.getId().getValue())
                .status(application.getStatus().name())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
