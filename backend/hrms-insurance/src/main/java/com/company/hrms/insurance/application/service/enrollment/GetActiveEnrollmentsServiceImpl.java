package com.company.hrms.insurance.application.service.enrollment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("getActiveEnrollmentsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GetActiveEnrollmentsServiceImpl implements QueryApiService<String, List<EnrollmentDetailResponse>> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    @Override
    public List<EnrollmentDetailResponse> getResponse(String employeeId, JWTModel currentUser, String... args)
            throws Exception {

        log.debug("查詢有效加保記錄: employeeId={}", employeeId);

        List<InsuranceEnrollment> enrollments = enrollmentRepository.findAllActiveByEmployeeId(employeeId);

        return enrollments.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
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
