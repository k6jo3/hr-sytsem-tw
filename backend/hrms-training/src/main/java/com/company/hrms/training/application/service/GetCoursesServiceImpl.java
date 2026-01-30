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
import com.company.hrms.training.infrastructure.entity.TrainingCourseEntity;
import com.company.hrms.training.infrastructure.repository.TrainingCourseQueryRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢課程列表服務
 * 使用 QueryBuilder.fromDto() 進行宣告式查詢
 */
@Service("getCoursesServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCoursesServiceImpl implements QueryApiService<GetCoursesRequest, Page<TrainingCourseResponse>> {

    private final TrainingCourseQueryRepository courseRepository;

    @Override
    public Page<TrainingCourseResponse> getResponse(GetCoursesRequest request, JWTModel currentUser, String... args) {
        // 使用 QueryBuilder 從 Request DTO 自動建構查詢條件 (宣告式查詢)
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // 取得分頁資訊 (從 PageRequest 轉換)
        Pageable pageable = request.toPageable();

        // 執行查詢
        Page<TrainingCourseEntity> coursePage = courseRepository.findPage(query, pageable);

        // 轉換為 DTO
        List<TrainingCourseResponse> responseList = new ArrayList<>();
        for (TrainingCourseEntity entity : coursePage.getContent()) {
            responseList.add(toResponse(entity));
        }

        return new PageImpl<>(responseList, pageable, coursePage.getTotalElements());
    }

    // TODO: 重構此方法為 Factory 或 Mapper
    private TrainingCourseResponse toResponse(TrainingCourseEntity course) {
        // TODO: 程式太長，建議使用 Factory 或 Mapper
        TrainingCourseResponse res = new TrainingCourseResponse();
        res.setCourseId(course.getCourseId());
        res.setCourseCode(course.getCourseCode());
        res.setCourseName(course.getName());

        res.setCourseType(course.getType());
        res.setDeliveryMode(course.getMode());
        res.setCategory(course.getCategory());
        res.setDescription(course.getDescription());
        res.setInstructor(course.getInstructor());
        res.setInstructorInfo(course.getInstructorInfo());
        res.setDurationHours(course.getDurationHours());
        res.setMaxParticipants(course.getMaxParticipants());
        res.setMinParticipants(course.getMinParticipants());
        res.setCurrentEnrollments(course.getCurrentEnrollments());
        res.setStartDate(course.getStartDate());
        res.setEndDate(course.getEndDate());
        res.setStartTime(course.getStartTime());
        res.setEndTime(course.getEndTime());
        res.setLocation(course.getLocation());
        res.setCost(course.getCost());
        res.setIsMandatory(course.getIsMandatory());
        res.setTargetAudience(course.getTargetAudience());
        res.setPrerequisites(course.getPrerequisites());
        res.setEnrollmentDeadline(course.getEnrollmentDeadline());
        res.setStatus(course.getStatus());
        res.setCreatedBy(course.getCreatedBy());
        return res;
    }
}
