package com.company.hrms.training.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.training.domain.event.EnrollmentApprovedEvent;
import com.company.hrms.training.domain.event.EnrollmentCancelledEvent;
import com.company.hrms.training.domain.event.EnrollmentCreatedEvent;
import com.company.hrms.training.domain.event.EnrollmentRejectedEvent;
import com.company.hrms.training.domain.event.TrainingCompletedEvent;
import com.company.hrms.training.domain.model.valueobject.EnrollmentId;
import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;

import lombok.Getter;

@Getter
public class TrainingEnrollment extends AggregateRoot<EnrollmentId> {

    private String courseId; // 保持 String 或用 CourseId，這裡用 String 方便與 Event 映射，但正規應為 CourseId
    // 因為要發 event，需要 course info，但 aggregate 只有 ID。
    // 在 DDD 中，aggregate 不直接持有另一個 aggregate，而是 ID。
    // 這裡我們假設需要從外部 service 獲取 course 資訊來發 event，或者傳入方法中。
    // 為了簡化，create 時傳入必要資訊。

    private String employeeId;

    private EnrollmentStatus status;
    private String reason;
    private String remarks;

    private String approvedBy;
    private LocalDateTime approvedAt;

    private String rejectedBy;
    private LocalDateTime rejectedAt;
    private String rejectReason; // 對應 API 的 reason，但避免變數名稱衝突用 rejectReason

    private String cancelledBy;
    private LocalDateTime cancelledAt;
    private String cancelReason;

    private boolean attendance;
    private BigDecimal attendedHours;
    private LocalDateTime attendedAt;

    private BigDecimal completedHours;
    private BigDecimal score;
    private Boolean passed;
    private String feedback;
    private LocalDateTime completedAt;

    private TrainingEnrollment(EnrollmentId id) {
        super(id);
    }

    public static TrainingEnrollment create(
            String courseId,
            String courseName,
            String employeeId,
            String employeeName,
            String managerId,
            String managerName,
            BigDecimal trainingHours,
            BigDecimal cost,
            String reason,
            String remarks) {

        TrainingEnrollment enrollment = new TrainingEnrollment(EnrollmentId.create());
        enrollment.courseId = courseId;
        enrollment.employeeId = employeeId;
        enrollment.reason = reason;
        enrollment.remarks = remarks;
        enrollment.status = EnrollmentStatus.REGISTERED;
        enrollment.createdAt = LocalDateTime.now();

        enrollment.registerEvent(EnrollmentCreatedEvent.create(
                enrollment.getId().toString(),
                courseId,
                courseName,
                employeeId,
                employeeName,
                managerId,
                managerName,
                trainingHours,
                cost,
                reason));

        return enrollment;
    }

    public static TrainingEnrollment reconstitute(
            EnrollmentId id,
            String courseId,
            String employeeId,
            EnrollmentStatus status,
            String reason,
            String remarks,
            String approvedBy,
            LocalDateTime approvedAt,
            String rejectedBy,
            LocalDateTime rejectedAt,
            String rejectReason,
            String cancelledBy,
            LocalDateTime cancelledAt,
            String cancelReason,
            boolean attendance,
            BigDecimal attendedHours,
            LocalDateTime attendedAt,
            BigDecimal completedHours,
            BigDecimal score,
            Boolean passed,
            String feedback,
            LocalDateTime completedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        TrainingEnrollment enrollment = new TrainingEnrollment(id);
        enrollment.courseId = courseId;
        enrollment.employeeId = employeeId;
        enrollment.status = status;
        enrollment.reason = reason;
        enrollment.remarks = remarks;
        enrollment.approvedBy = approvedBy;
        enrollment.approvedAt = approvedAt;
        enrollment.rejectedBy = rejectedBy;
        enrollment.rejectedAt = rejectedAt;
        enrollment.rejectReason = rejectReason;
        enrollment.cancelledBy = cancelledBy;
        enrollment.cancelledAt = cancelledAt;
        enrollment.cancelReason = cancelReason;
        enrollment.attendance = attendance;
        enrollment.attendedHours = attendedHours;
        enrollment.attendedAt = attendedAt;
        enrollment.completedHours = completedHours;
        enrollment.score = score;
        enrollment.passed = passed;
        enrollment.feedback = feedback;
        enrollment.completedAt = completedAt;
        enrollment.createdAt = createdAt;
        enrollment.updatedAt = updatedAt;

        return enrollment;
    }

    public void approve(
            String approverId,
            String courseName,
            String employeeName,
            String employeeEmail,
            String startDate,
            String location) {

        if (this.status != EnrollmentStatus.REGISTERED) {
            throw new IllegalStateException("只有待審核的報名可以審核");
        }

        this.status = EnrollmentStatus.APPROVED;
        this.approvedBy = approverId;
        this.approvedAt = LocalDateTime.now();
        this.touch();

        registerEvent(EnrollmentApprovedEvent.create(
                this.getId().toString(),
                this.courseId,
                courseName,
                this.employeeId,
                employeeName,
                employeeEmail,
                startDate,
                location,
                approverId));
    }

    public void reject(
            String rejecterId,
            String reason,
            String employeeName,
            String employeeEmail,
            String courseName) {

        if (this.status != EnrollmentStatus.REGISTERED) {
            throw new IllegalStateException("只有待審核的報名可以拒絕");
        }

        this.status = EnrollmentStatus.REJECTED;
        this.rejectedBy = rejecterId;
        this.rejectReason = reason;
        this.rejectedAt = LocalDateTime.now();
        this.touch();

        registerEvent(EnrollmentRejectedEvent.create(
                this.getId().toString(),
                this.employeeId,
                employeeName,
                employeeEmail,
                courseName,
                rejecterId,
                reason));
    }

    public void cancel(
            String cancellerId,
            String reason,
            String courseName) {

        if (this.status != EnrollmentStatus.REGISTERED && this.status != EnrollmentStatus.APPROVED) {
            throw new IllegalStateException("只有待審核或已核准的報名可以取消");
        }

        this.status = EnrollmentStatus.CANCELLED;
        this.cancelledBy = cancellerId;
        this.cancelReason = reason;
        this.cancelledAt = LocalDateTime.now();
        this.touch();

        registerEvent(EnrollmentCancelledEvent.create(
                this.getId().toString(),
                this.employeeId,
                this.courseId,
                courseName,
                cancellerId,
                reason));
    }

    public void confirmAttendance(boolean attended, BigDecimal attendedHours, String remarks) {
        if (this.status != EnrollmentStatus.APPROVED) {
            // 考慮可能有 walk-in 情況，或是已經 ATTENDED 想修正資料
            // 按照規格：流程是 REGISTERED -> APPROVED -> ATTENDED
            // 如果已經 COMPLETED 則不應再確認出席
            if (this.status == EnrollmentStatus.COMPLETED) {
                throw new IllegalStateException("已完成的訓練不可再變更出席狀態");
            }
            if (this.status != EnrollmentStatus.ATTENDED && this.status != EnrollmentStatus.APPROVED) {
                throw new IllegalStateException("只有已核准的報名可以確認出席");
            }
        }

        this.attendance = attended;

        if (attended) {
            this.status = EnrollmentStatus.ATTENDED;
            this.attendedHours = attendedHours;
            this.attendedAt = LocalDateTime.now();
        } else {
            this.status = EnrollmentStatus.NO_SHOW;
            this.attendedHours = BigDecimal.ZERO;
        }

        if (remarks != null) {
            this.remarks = remarks; // Append or replace? Using replace for simplicity
        }

        this.touch();
    }

    public void complete(
            BigDecimal completedHours,
            BigDecimal score,
            Boolean passed,
            String feedback,
            String employeeName,
            String courseName,
            String courseCategory) {

        // 允許從 ATTENDED 轉 COMPLETED，也允許直接從 APPROVED 轉 COMPLETED (如果流程簡化的話)，
        // 但根據規格 3.7，報名狀態必須為 ATTENDED
        if (this.status != EnrollmentStatus.ATTENDED) {
            // 例外：若出席確認被跳過，系統可能允許直接完成？嚴格模式下依據規格。
            throw new IllegalStateException("必須先確認出席(ATTENDED)才能完成訓練");
        }

        this.status = EnrollmentStatus.COMPLETED;
        this.completedHours = completedHours;
        this.score = score;
        this.passed = passed != null ? passed : true;
        this.feedback = feedback;
        this.completedAt = LocalDateTime.now();
        this.touch();

        registerEvent(TrainingCompletedEvent.create(
                this.getId().toString(),
                this.employeeId,
                employeeName,
                this.courseId,
                courseName,
                courseCategory,
                completedHours,
                LocalDate.now().toString(),
                score,
                this.passed));
    }
}
