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
import com.company.hrms.training.api.request.GetMyTrainingsRequest;
import com.company.hrms.training.api.response.TrainingEnrollmentResponse;
import com.company.hrms.training.application.assembler.TrainingEnrollmentAssembler;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;
import com.company.hrms.training.infrastructure.repository.TrainingEnrollmentQueryRepository;

import lombok.RequiredArgsConstructor;

/**
 * ?�詢?��?訓練紀?��???
 */
@Service("getMyTrainingsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@SuppressWarnings("null")
public class GetMyTrainingsServiceImpl
        implements QueryApiService<GetMyTrainingsRequest, Page<TrainingEnrollmentResponse>> {

    private final TrainingEnrollmentQueryRepository enrollmentRepository;
    private final TrainingEnrollmentAssembler trainingEnrollmentAssembler;

    @Override
    public Page<TrainingEnrollmentResponse> getResponse(GetMyTrainingsRequest request, JWTModel currentUser,
            String... args) {

        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .eq("employee_id", currentUser.getUserId())
                .build();

        Pageable pageable = request.toPageable();

        Page<TrainingEnrollmentEntity> page = enrollmentRepository.findPage(query, pageable);

        List<TrainingEnrollmentResponse> responseList = new ArrayList<>();
        for (TrainingEnrollmentEntity enrollment : page.getContent()) {
            responseList.add(trainingEnrollmentAssembler.toResponse(enrollment));
        }

        return new PageImpl<>(responseList, pageable, page.getTotalElements());
    }

}
