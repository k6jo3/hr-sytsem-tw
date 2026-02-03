package com.company.hrms.training.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.training.api.response.TrainingCourseResponse;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.infrastructure.entity.TrainingCourseEntity;

/**
 * 訓練課程 Assembler
 * 負責 Entity 與 DTO 之間的轉換
 */
@Component
public class TrainingCourseAssembler {

    public static TrainingCourseResponse toResponse(TrainingCourseEntity course) {
        if (course == null) {
            return null;
        }

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

    public static TrainingCourseResponse toResponse(TrainingCourse course) {
        if (course == null) {
            return null;
        }

        TrainingCourseResponse res = new TrainingCourseResponse();
        res.setCourseId(course.getId().getValue());
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
