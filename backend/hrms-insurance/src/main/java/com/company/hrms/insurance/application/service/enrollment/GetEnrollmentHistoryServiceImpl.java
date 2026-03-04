package com.company.hrms.insurance.application.service.enrollment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.application.assembler.EnrollmentResponseAssembler;
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
    private final EnrollmentResponseAssembler assembler;

    @Override
    public PageResponse<EnrollmentDetailResponse> getResponse(String id, JWTModel currentUser, String... args)
            throws Exception {

        log.debug("查詢投保歷程: enrollmentId={}", id);

        InsuranceEnrollment enrollment = enrollmentRepository.findById(new EnrollmentId(id))
                .orElse(null);

        if (enrollment == null) {
            return PageResponse.empty();
        }

        EnrollmentDetailResponse detail = assembler.toDetailResponse(enrollment, null);
        return PageResponse.of(List.of(detail), 1, 10, 1);
    }
}
