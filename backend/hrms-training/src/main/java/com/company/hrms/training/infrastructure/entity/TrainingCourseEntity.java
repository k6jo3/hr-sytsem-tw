package com.company.hrms.training.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.company.hrms.training.domain.model.valueobject.CourseCategory;
import com.company.hrms.training.domain.model.valueobject.CourseStatus;
import com.company.hrms.training.domain.model.valueobject.CourseType;
import com.company.hrms.training.domain.model.valueobject.DeliveryMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "training_courses")
@Getter
@Setter
public class TrainingCourseEntity {

    @Id
    @Column(name = "course_id")
    private String courseId;

    @Column(name = "course_code")
    private String courseCode;

    @Column(name = "course_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_type")
    private CourseType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_mode")
    private DeliveryMode mode;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private CourseCategory category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "instructor")
    private String instructor;

    @Column(name = "instructor_info", columnDefinition = "TEXT")
    private String instructorInfo;

    @Column(name = "duration_hours")
    private BigDecimal durationHours;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "min_participants")
    private Integer minParticipants;

    @Column(name = "current_enrollments")
    private Integer currentEnrollments;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "location")
    private String location;

    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "target_audience", columnDefinition = "TEXT")
    private String targetAudience;

    @Column(name = "prerequisites", columnDefinition = "TEXT")
    private String prerequisites;

    @Column(name = "enrollment_deadline")
    private LocalDate enrollmentDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CourseStatus status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Integer is_deleted = 0;
}
