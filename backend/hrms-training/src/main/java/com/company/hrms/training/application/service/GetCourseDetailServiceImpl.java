package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.training.api.response.TrainingCourseResponse;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

import lombok.RequiredArgsConstructor;

@Service("getCourseDetailServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCourseDetailServiceImpl implements QueryApiService<String, TrainingCourseResponse> {

    private final ITrainingCourseRepository courseRepository;

    @Override
    public TrainingCourseResponse getResponse(String id, JWTModel currentUser, String... args) {
        TrainingCourse course = courseRepository.findById(CourseId.from(id))
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));

        return toResponse(course);
    }

    private TrainingCourseResponse toResponse(TrainingCourse course) {
        TrainingCourseResponse res = new TrainingCourseResponse();
        res.setCourseId(course.getId().toString());
        res.setCourseCode(course.getCourseCode());
        res.setCourseName(course.getCourseName());
        res.setCourseType(course.getCourseType());
        res.setDeliveryMode(course.getDeliveryMode());
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
