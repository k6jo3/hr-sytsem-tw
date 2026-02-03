package com.company.hrms.training.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.training.domain.event.CourseClosedEvent;
import com.company.hrms.training.domain.event.CourseCompletedEvent;
import com.company.hrms.training.domain.event.CourseCreatedEvent;
import com.company.hrms.training.domain.event.CoursePublishedEvent;
import com.company.hrms.training.domain.model.valueobject.CourseCategory;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.model.valueobject.CourseStatus;
import com.company.hrms.training.domain.model.valueobject.CourseType;
import com.company.hrms.training.domain.model.valueobject.DeliveryMode;

import lombok.Getter;

@Getter
public class TrainingCourse extends AggregateRoot<CourseId> {

    private String courseCode;
    private String courseName;
    private CourseType courseType;
    private DeliveryMode deliveryMode;
    private CourseCategory category;
    private String description;

    private String instructor;
    private String instructorInfo; // JSON string for detailed info

    private BigDecimal durationHours;
    private Integer maxParticipants;
    private Integer minParticipants;
    private Integer currentEnrollments;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String location;
    private BigDecimal cost;
    private Boolean isMandatory;
    private String targetAudience; // JSON array string
    private String prerequisites;
    private LocalDate enrollmentDeadline;

    private CourseStatus status;
    private String createdBy;

    private TrainingCourse(CourseId id) {
        super(id);
    }

    public static TrainingCourse create(
            String courseCode,
            String courseName,
            CourseType courseType,
            DeliveryMode deliveryMode,
            BigDecimal durationHours,
            LocalDate startDate,
            LocalDate endDate,
            String createdBy) {

        validateCourseName(courseName);
        validateDates(startDate, endDate);
        validateDuration(durationHours);

        TrainingCourse course = new TrainingCourse(CourseId.create());
        course.courseCode = courseCode;
        course.courseName = courseName;
        course.courseType = courseType;
        course.deliveryMode = deliveryMode;
        course.durationHours = durationHours;
        course.startDate = startDate;
        course.endDate = endDate;
        course.createdBy = createdBy;
        course.status = CourseStatus.DRAFT;
        course.currentEnrollments = 0;
        course.createdAt = LocalDateTime.now();

        course.registerEvent(CourseCreatedEvent.create(
                course.getId(),
                courseCode,
                courseName,
                courseType,
                durationHours,
                startDate,
                createdBy));

        return course;
    }

    public static TrainingCourse reconstitute(
            CourseId id,
            String courseCode,
            String courseName,
            CourseType courseType,
            DeliveryMode deliveryMode,
            CourseCategory category,
            String description,
            String instructor,
            String instructorInfo,
            BigDecimal durationHours,
            Integer maxParticipants,
            Integer minParticipants,
            Integer currentEnrollments,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime,
            String location,
            BigDecimal cost,
            Boolean isMandatory,
            String targetAudience,
            String prerequisites,
            LocalDate enrollmentDeadline,
            CourseStatus status,
            String createdBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        TrainingCourse course = new TrainingCourse(id);
        course.courseCode = courseCode;
        course.courseName = courseName;
        course.courseType = courseType;
        course.deliveryMode = deliveryMode;
        course.category = category;
        course.description = description;
        course.instructor = instructor;
        course.instructorInfo = instructorInfo;
        course.durationHours = durationHours;
        course.maxParticipants = maxParticipants;
        course.minParticipants = minParticipants;
        course.currentEnrollments = currentEnrollments;
        course.startDate = startDate;
        course.endDate = endDate;
        course.startTime = startTime;
        course.endTime = endTime;
        course.location = location;
        course.cost = cost;
        course.isMandatory = isMandatory;
        course.targetAudience = targetAudience;
        course.prerequisites = prerequisites;
        course.enrollmentDeadline = enrollmentDeadline;
        course.status = status;
        course.createdBy = createdBy;
        course.createdAt = createdAt;
        course.updatedAt = updatedAt;

        return course;
    }

    public void updateInfo(
            String courseName,
            String description,
            Integer maxParticipants,
            String location) {

        validateEditable();

        if (courseName != null) {
            validateCourseName(courseName);
            this.courseName = courseName;
        }

        if (description != null) {
            this.description = description;
        }

        if (maxParticipants != null) {
            if (this.currentEnrollments > maxParticipants) {
                throw new IllegalArgumentException("最大人數不可少於目前報名人數");
            }
            this.maxParticipants = maxParticipants;
        }

        if (location != null) {
            this.location = location;
        }

        this.touch();
    }

    public void publish() {
        if (this.status != CourseStatus.DRAFT) {
            throw new IllegalStateException("只有草稿課程可以發布");
        }

        if (this.startDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("課程開始日期不能是過去");
        }

        this.status = CourseStatus.OPEN;
        this.touch();

        this.registerEvent(CoursePublishedEvent.create(
                this.getId().toString(),
                this.courseName,
                this.isMandatory != null ? this.isMandatory : false,
                this.targetAudience,
                this.startDate,
                this.enrollmentDeadline));
    }

    public void close(String reason) {
        if (this.status != CourseStatus.OPEN) {
            throw new IllegalStateException("只有開放報名的課程可以關閉");
        }

        this.status = CourseStatus.CLOSED;
        this.touch();

        this.registerEvent(CourseClosedEvent.create(
                this.getId().toString(),
                this.courseName,
                reason));
    }

    public void complete(Integer completedCount, Integer noShowCount) {
        if (this.status != CourseStatus.CLOSED) {
            throw new IllegalStateException("只有已截止報名的課程可以完成");
        }

        if (this.endDate != null && this.endDate.isAfter(LocalDate.now())) {
            throw new IllegalStateException("課程尚未結束");
        }

        this.status = CourseStatus.COMPLETED;
        this.touch();

        this.registerEvent(CourseCompletedEvent.create(
                this.getId().toString(),
                this.courseName,
                completedCount != null ? completedCount : 0,
                noShowCount != null ? noShowCount : 0));
    }

    public boolean canEnroll() {
        return this.status == CourseStatus.OPEN &&
                (this.enrollmentDeadline == null || !LocalDate.now().isAfter(this.enrollmentDeadline)) &&
                (this.maxParticipants == null || this.currentEnrollments < this.maxParticipants);
    }

    /**
     * 增加報名人數
     */
    public void incrementEnrollmentCount() {
        if (!canEnroll()) {
            throw new IllegalStateException("目前無法報名此課程");
        }
        this.currentEnrollments++;
        this.touch();
    }

    /**
     * 減少報名人數（用於退選或審核不通過）
     */
    public void decrementEnrollmentCount() {
        if (this.currentEnrollments > 0) {
            this.currentEnrollments--;
            this.touch();
        }
    }

    // === Domain Logic Validations ===

    private static void validateCourseName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("課程名稱不能為空");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("課程名稱過長");
        }
    }

    private static void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("開始與結束日期不能為空");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("結束日期不能早於開始日期");
        }
        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("開始日期不能是過去");
        }
    }

    private static void validateDuration(BigDecimal hours) {
        if (hours == null || hours.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("訓練時數必須大於 0");
        }
    }

    private void validateEditable() {
        if (this.status == CourseStatus.COMPLETED || this.status == CourseStatus.CANCELLED) {
            throw new IllegalStateException("已結束或取消的課程無法編輯");
        }
        // CLOSED 狀態如果沒有報名資料可能可以編輯，但為求簡單暫時只允許 DRAFT 和 OPEN
        if (this.status == CourseStatus.CLOSED) {
            throw new IllegalStateException("已截止報名的課程無法編輯");
        }
    }

    // Setters for optional fields
    public void setCategory(CourseCategory category) {
        validateEditable();
        this.category = category;
    }

    public void setDescription(String description) {
        validateEditable();
        this.description = description;
    }

    public void setInstructor(String instructor) {
        validateEditable();
        this.instructor = instructor;
    }

    public void setInstructorInfo(String instructorInfo) {
        validateEditable();
        this.instructorInfo = instructorInfo;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        validateEditable();
        this.maxParticipants = maxParticipants;
    }

    public void setMinParticipants(Integer minParticipants) {
        validateEditable();
        this.minParticipants = minParticipants;
    }

    public void setTimes(LocalTime startTime, LocalTime endTime) {
        validateEditable();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setLocation(String location) {
        validateEditable();
        this.location = location;
    }

    public void setCost(BigDecimal cost) {
        validateEditable();
        this.cost = cost;
    }

    public void setMandatory(Boolean mandatory) {
        validateEditable();
        isMandatory = mandatory;
    }

    public void setTargetAudience(String targetAudience) {
        validateEditable();
        this.targetAudience = targetAudience;
    }

    public void setPrerequisites(String prerequisites) {
        validateEditable();
        this.prerequisites = prerequisites;
    }

    public void setEnrollmentDeadline(LocalDate enrollmentDeadline) {
        validateEditable();
        if (enrollmentDeadline != null && enrollmentDeadline.isAfter(this.startDate)) {
            throw new IllegalArgumentException("報名截止日不能晚於課程開始日");
        }
        this.enrollmentDeadline = enrollmentDeadline;
    }
}
