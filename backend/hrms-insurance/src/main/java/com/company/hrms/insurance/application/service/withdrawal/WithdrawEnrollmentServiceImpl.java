package com.company.hrms.insurance.application.service.withdrawal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.insurance.api.request.WithdrawEnrollmentRequest;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.application.service.withdrawal.context.WithdrawalContext;
import com.company.hrms.insurance.application.service.withdrawal.task.LoadEnrollmentTask;
import com.company.hrms.insurance.application.service.withdrawal.task.PerformWithdrawalTask;
import com.company.hrms.insurance.application.service.withdrawal.task.SaveWithdrawalTask;
import com.company.hrms.insurance.application.service.withdrawal.task.ValidateWithdrawalTask;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("withdrawEnrollmentServiceImpl")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WithdrawEnrollmentServiceImpl
        implements CommandApiService<WithdrawEnrollmentRequest, EnrollmentDetailResponse> {

    private final LoadEnrollmentTask loadEnrollmentTask;
    private final ValidateWithdrawalTask validateWithdrawalTask;
    private final PerformWithdrawalTask performWithdrawalTask;
    private final SaveWithdrawalTask saveWithdrawalTask;

    @Override
    public EnrollmentDetailResponse execCommand(WithdrawEnrollmentRequest request, JWTModel currentUser, String... args)
            throws Exception {

        String enrollmentId = args[0];
        log.info("執行退保: enrollmentId={}, date={}", enrollmentId, request.getWithdrawDate());

        // 建立 Context
        WithdrawalContext context = new WithdrawalContext(enrollmentId, request, currentUser.getTenantId());

        // 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadEnrollmentTask)
                .next(validateWithdrawalTask)
                .next(performWithdrawalTask)
                .next(saveWithdrawalTask)
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
