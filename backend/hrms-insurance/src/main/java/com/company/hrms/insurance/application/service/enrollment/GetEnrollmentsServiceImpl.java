package com.company.hrms.insurance.application.service.enrollment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.insurance.api.request.GetEnrollmentListRequest;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.application.assembler.EnrollmentResponseAssembler;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("getEnrollmentsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GetEnrollmentsServiceImpl
        implements QueryApiService<GetEnrollmentListRequest, PageResponse<EnrollmentDetailResponse>> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;
    private final EnrollmentResponseAssembler assembler;

    @Override
    public PageResponse<EnrollmentDetailResponse> getResponse(GetEnrollmentListRequest request, JWTModel currentUser,
            String... args)
            throws Exception {

        String employeeId = request != null ? request.getEmployeeId() : null;
        log.debug("查詢加退保記錄: employeeId={}", employeeId);

        List<InsuranceEnrollment> enrollments;
        if (employeeId != null && !employeeId.isBlank()) {
            enrollments = enrollmentRepository.findByEmployeeId(employeeId);
        } else {
            enrollments = enrollmentRepository.findAll();
        }

        List<EnrollmentDetailResponse> items = enrollments.stream()
                .map(e -> assembler.toDetailResponse(e, null))
                .collect(Collectors.toList());

        return PageResponse.of(items, 1, 20, items.size());
    }
}
