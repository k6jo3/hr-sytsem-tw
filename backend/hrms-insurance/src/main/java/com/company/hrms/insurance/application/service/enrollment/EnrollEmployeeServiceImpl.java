package com.company.hrms.insurance.application.service.enrollment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.insurance.api.request.EnrollEmployeeRequest;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.application.service.enrollment.task.CalculateEnrollmentFeesTask;
import com.company.hrms.insurance.application.service.enrollment.task.CreateEnrollmentRecordsTask;
import com.company.hrms.insurance.application.service.enrollment.task.FindInsuranceLevelTask;
import com.company.hrms.insurance.application.service.enrollment.task.LoadInsuranceUnitTask;
import com.company.hrms.insurance.application.service.enrollment.task.SaveEnrollmentTask;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("enrollEmployeeServiceImpl")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EnrollEmployeeServiceImpl implements CommandApiService<EnrollEmployeeRequest, EnrollmentDetailResponse> {

    private final LoadInsuranceUnitTask loadInsuranceUnitTask;
    private final FindInsuranceLevelTask findInsuranceLevelTask;
    private final CreateEnrollmentRecordsTask createEnrollmentRecordsTask;
    private final CalculateEnrollmentFeesTask calculateEnrollmentFeesTask;
    private final SaveEnrollmentTask saveEnrollmentTask;

    @Override
    public EnrollmentDetailResponse execCommand(EnrollEmployeeRequest request, JWTModel currentUser, String... args)
            throws Exception {

        log.info("執行員工加保: employeeId={}, date={}", request.getEmployeeId(), request.getEnrollDate());

        // 建立 Context
        EnrollmentContext context = new EnrollmentContext(request, currentUser.getTenantId());

        // 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadInsuranceUnitTask)
                .next(findInsuranceLevelTask)
                .next(createEnrollmentRecordsTask)
                .next(calculateEnrollmentFeesTask)
                .next(saveEnrollmentTask)
                .execute();

        // 建構回應 (取第一筆記錄代表，實際可能有多筆)
        InsuranceEnrollment primaryEnrollment = context.getEnrollments().get(0);

        return EnrollmentDetailResponse.builder()
                .enrollmentId(primaryEnrollment.getId().getValue())
                .employeeId(primaryEnrollment.getEmployeeId())
                .insuranceType(primaryEnrollment.getInsuranceType().name())
                .insuranceTypeDisplay(primaryEnrollment.getInsuranceType().getDisplayName())
                .status(primaryEnrollment.getStatus().name())
                .statusDisplay(primaryEnrollment.getStatus().getDisplayName())
                .enrollDate(primaryEnrollment.getEnrollDate().toString())
                .monthlySalary(primaryEnrollment.getMonthlySalary())
                .build();
    }
}
