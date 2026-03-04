package com.company.hrms.insurance.application.service.enrollment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.application.assembler.EnrollmentResponseAssembler;
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
    private final EnrollmentResponseAssembler assembler;

    @Override
    public List<EnrollmentDetailResponse> getResponse(String employeeId, JWTModel currentUser, String... args)
            throws Exception {

        log.debug("查詢有效加保記錄: employeeId={}", employeeId);

        List<InsuranceEnrollment> enrollments = enrollmentRepository.findAllActiveByEmployeeId(employeeId);

        return enrollments.stream()
                .map(e -> assembler.toDetailResponse(e, null))
                .collect(Collectors.toList());
    }
}
