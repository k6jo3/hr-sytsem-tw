package com.company.hrms.insurance.application.service.enrollment;

import org.springframework.stereotype.Service;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentId;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢加退保記錄詳情服務實作
 */
@Service("getEnrollmentDetailServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class GetEnrollmentDetailServiceImpl implements QueryApiService<String, EnrollmentDetailResponse> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    @Override
    public EnrollmentDetailResponse getResponse(String id, JWTModel currentUser, String... args) throws Exception {
        log.info("查詢加退保記錄詳情: enrollmentId={}", id);

        var enrollment = enrollmentRepository.findById(new EnrollmentId(id))
                .orElseThrow(() -> new EntityNotFoundException("ENROLLMENT_NOT_FOUND", "找不到加退保記錄: " + id));

        return EnrollmentDetailResponse.builder()
                .enrollmentId(enrollment.getId().getValue())
                .employeeId(enrollment.getEmployeeId())
                .insuranceType(enrollment.getInsuranceType().name())
                .insuranceTypeDisplay(enrollment.getInsuranceType().getDisplayName())
                .status(enrollment.getStatus().name())
                .statusDisplay(enrollment.getStatus().getDisplayName())
                .enrollDate(enrollment.getEnrollDate().toString())
                .withdrawDate(enrollment.getWithdrawDate() != null ? enrollment.getWithdrawDate().toString() : null)
                .monthlySalary(enrollment.getMonthlySalary())
                // 注意：這裡簡化處理，實際可能需要查詢員工姓名和單位名稱
                .build();
    }
}
