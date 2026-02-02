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
 * ?¥è©¢èª²ç??—è¡¨?å?
 * ä½¿ç”¨ QueryBuilder.fromDto() ?²è?å®??å¼æŸ¥è©?
 */
@Service("getCoursesServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCoursesServiceImpl implements QueryApiService<GetCoursesRequest, Page<TrainingCourseResponse>> {

    private final TrainingCourseQueryRepository courseRepository;

    @Override
    public Page<TrainingCourseResponse> getResponse(GetCoursesRequest request, JWTModel currentUser, String... args) {
        // ä½¿ç”¨ QueryBuilder å¾?Request DTO ?ªå?å»ºæ??¥è©¢æ¢ä»¶ (å®??å¼æŸ¥è©?
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // ?–å??†é?è³‡è? (å¾?PageRequest è½‰æ?)
        Pageable pageable = request.toPageable();

        // ?·è??¥è©¢
        Page<TrainingCourseEntity> coursePage = courseRepository.findPage(query, pageable);

        // è½‰æ???DTO
        List<TrainingCourseResponse> responseList = new ArrayList<>();
        for (TrainingCourseEntity entity : coursePage.getContent()) {
            responseList.add(toResponse(entity));
        }

        return new PageImpl<>(responseList, pageable, coursePage.getTotalElements());
    }

    // TODO: ?æ?æ­¤æ–¹æ³•ç‚º Factory ??Mapper
    private TrainingCourseResponse toResponse(TrainingCourseEntity course) {
        // TODO: ç¨‹å?å¤ªé•·ï¼Œå»ºè­°ä½¿??Factory ??Mapper
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
