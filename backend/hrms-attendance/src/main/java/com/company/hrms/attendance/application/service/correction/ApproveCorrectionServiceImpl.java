package com.company.hrms.attendance.application.service.correction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.attendance.ApproveCorrectionRequest;
import com.company.hrms.attendance.api.response.attendance.ApproveCorrectionResponse;
import com.company.hrms.attendance.application.service.correction.context.ApproveCorrectionContext;
import com.company.hrms.attendance.application.service.correction.task.LoadCorrectionTask;
import com.company.hrms.attendance.application.service.correction.task.PerformApproveCorrectionTask;
import com.company.hrms.attendance.application.service.correction.task.SaveApproveCorrectionTask;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;

/**
 * 補卡申請審核服務
 */
@Service("approveCorrectionServiceImpl")
@RequiredArgsConstructor
@Transactional
public class ApproveCorrectionServiceImpl
        implements CommandApiService<ApproveCorrectionRequest, ApproveCorrectionResponse> {

    private final LoadCorrectionTask loadCorrectionTask;
    private final PerformApproveCorrectionTask performApproveCorrectionTask;
    private final SaveApproveCorrectionTask saveApproveCorrectionTask;

    @Override
    public ApproveCorrectionResponse execCommand(ApproveCorrectionRequest request, JWTModel currentUser, String... args)
            throws Exception {
        String correctionId = args.length > 0 ? args[0] : null;
        if (correctionId == null) {
            throw new IllegalArgumentException("Correction ID is required");
        }

        var context = new ApproveCorrectionContext(request, currentUser.getTenantId());
        context.setCorrectionId(correctionId);

        BusinessPipeline.start(context)
                .next(loadCorrectionTask)
                .next(performApproveCorrectionTask)
                .next(saveApproveCorrectionTask)
                .execute();

        var application = context.getApplication();
        return ApproveCorrectionResponse.builder()
                .correctionId(application.getId().getValue())
                .status(application.getStatus().name())
                .approvedBy(currentUser.getDisplayName()) // 假設當前使用者即為審核人
                .approvedAt(java.time.LocalDateTime.now()) // 簡單回傳當前時間，若有持久化需求需調整 Aggregate
                .build();
    }
}
