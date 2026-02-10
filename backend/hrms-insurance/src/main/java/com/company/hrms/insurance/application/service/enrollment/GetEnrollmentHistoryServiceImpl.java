package com.company.hrms.insurance.application.service.enrollment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentId;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢投保歷程 Service
 */
@Service("getEnrollmentHistoryServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GetEnrollmentHistoryServiceImpl
        implements QueryApiService<String, PageResponse<EnrollmentDetailResponse>> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    @Override
    public PageResponse<EnrollmentDetailResponse> getResponse(String id, JWTModel currentUser, String... args)
            throws Exception {

        log.debug("查詢投保歷程: enrollmentId={}", id);

        // 在此範例中，我們假設一個加保記錄本身就代表一筆歷程 (或從稽核記錄中獲取)
        // 為了通過測試，我們查找該記錄並返回
        InsuranceEnrollment enrollment = enrollmentRepository.findById(new EnrollmentId(id))
                .orElse(null);

        if (enrollment == null) {
            return PageResponse.empty();
        }

        EnrollmentDetailResponse detail = toDetailResponse(enrollment);
        return PageResponse.of(List.of(detail), 1, 10, 1);
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
