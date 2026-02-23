package com.company.hrms.training.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.training.api.request.GetEnrollmentsRequest;
import com.company.hrms.training.api.response.TrainingEnrollmentResponse;
import com.company.hrms.training.application.assembler.TrainingEnrollmentAssembler;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;
import com.company.hrms.training.infrastructure.repository.TrainingEnrollmentQueryRepository;

import lombok.RequiredArgsConstructor;

/**
 * ?�詢?��??�表?��?
 * 使用 QueryBuilder.fromDto() ?��?�??式查�?
 */
@Service("getEnrollmentsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class GetEnrollmentsServiceImpl
        implements QueryApiService<GetEnrollmentsRequest, Page<TrainingEnrollmentResponse>> {

    private final TrainingEnrollmentQueryRepository enrollmentRepository;
    private final TrainingEnrollmentAssembler trainingEnrollmentAssembler;

    @Override
    public Page<TrainingEnrollmentResponse> getResponse(GetEnrollmentsRequest request, JWTModel currentUser,
            String... args) {
        // 使用 QueryBuilder �?Request DTO ?��?建�??�詢條件 (�??式查�?
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // ?��??��?資�? (�?PageRequest 轉�?)
        Pageable pageable = request.toPageable();

        // ?��??�詢
        Page<TrainingEnrollmentEntity> page = enrollmentRepository.findPage(query, pageable);

        // 轉�???DTO
        List<TrainingEnrollmentResponse> responseList = new ArrayList<>();
        for (TrainingEnrollmentEntity enrollment : page.getContent()) {
            responseList.add(trainingEnrollmentAssembler.toResponse(enrollment));
        }

        return new PageImpl<>(responseList, pageable, page.getTotalElements());
    }

}
