package com.company.hrms.insurance.application.service.adjustment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.insurance.api.request.AdjustLevelRequest;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.application.service.adjustment.context.AdjustmentContext;
import com.company.hrms.insurance.application.service.adjustment.task.FindNewLevelTask;
import com.company.hrms.insurance.application.service.adjustment.task.LoadEnrollmentForAdjustmentTask;
import com.company.hrms.insurance.application.service.adjustment.task.PerformAdjustmentTask;
import com.company.hrms.insurance.application.service.adjustment.task.SaveAdjustmentTask;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("adjustLevelServiceImpl")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdjustLevelServiceImpl implements CommandApiService<AdjustLevelRequest, EnrollmentDetailResponse> {

    private final LoadEnrollmentForAdjustmentTask loadEnrollmentForAdjustmentTask;
    private final FindNewLevelTask findNewLevelTask;
    private final PerformAdjustmentTask performAdjustmentTask;
    private final SaveAdjustmentTask saveAdjustmentTask;

    @Override
    public EnrollmentDetailResponse execCommand(AdjustLevelRequest request, JWTModel currentUser, String... args)
            throws Exception {

        String enrollmentId = args[0];
        log.info("執行調整級距: enrollmentId={}, newSalary={}", enrollmentId, request.getNewMonthlySalary());

        // 建立 Context
        AdjustmentContext context = new AdjustmentContext(enrollmentId, request, currentUser.getTenantId());

        // 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadEnrollmentForAdjustmentTask)
                .next(findNewLevelTask)
                .next(performAdjustmentTask)
                .next(saveAdjustmentTask)
                .execute();

        // 建構回應
        return toDetailResponse(context.getEnrollment());
    }

    private EnrollmentDetailResponse toDetailResponse(InsuranceEnrollment enrollment) {
        return EnrollmentDetailResponse.builder()
                .enrollmentId(enrollment.getId().getValue())
                .employeeId(enrollment.getEmployeeId())
                .insuranceType(enrollment.getInsuranceType().name())
                .insuranceTypeDisplay(enrollment.getInsuranceType().getDisplayName())
                .status(enrollment.getStatus().name())
                .statusDisplay(enrollment.getStatus().getDisplayName())
                .enrollDate(enrollment.getEnrollDate().toString())
                .withdrawDate(enrollment.getWithdrawDate() != null
                        ? enrollment.getWithdrawDate().toString()
                        : null)
                .monthlySalary(enrollment.getMonthlySalary())
                .build();
    }
}
