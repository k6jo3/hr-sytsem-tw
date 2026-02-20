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
import com.company.hrms.training.api.request.GetCoursesRequest;
import com.company.hrms.training.api.response.TrainingCourseResponse;
import com.company.hrms.training.application.assembler.TrainingCourseAssembler;
import com.company.hrms.training.infrastructure.entity.TrainingCourseEntity;
import com.company.hrms.training.infrastructure.repository.TrainingCourseQueryRepository;

import lombok.RequiredArgsConstructor;

/**
 * ?�詢課�??�表?��?
 * 使用 QueryBuilder.fromDto() ?��?�??式查�?
 */
@Service("getCoursesServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@SuppressWarnings("null")
public class GetCoursesServiceImpl implements QueryApiService<GetCoursesRequest, Page<TrainingCourseResponse>> {

    private final TrainingCourseQueryRepository courseRepository;

    @Override
    public Page<TrainingCourseResponse> getResponse(GetCoursesRequest request, JWTModel currentUser, String... args) {
        // 使用 QueryBuilder �?Request DTO ?��?建�??�詢條件 (�??式查�?
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // ?��??��?資�? (�?PageRequest 轉�?)
        Pageable pageable = request.toPageable();

        // ?��??�詢
        Page<TrainingCourseEntity> coursePage = courseRepository.findPage(query, pageable);

        // 轉�???DTO
        List<TrainingCourseResponse> responseList = new ArrayList<>();
        for (TrainingCourseEntity entity : coursePage.getContent()) {
            responseList.add(TrainingCourseAssembler.toResponse(entity));
        }

        return new PageImpl<>(responseList, pageable, coursePage.getTotalElements());
    }

}
