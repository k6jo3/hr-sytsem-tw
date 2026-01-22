package com.company.hrms.training.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "報名詳細資訊回應")
public class TrainingEnrollmentResponse {

    @Schema(description = "報名ID")
    private String enrollmentId;

    @Schema(description = "課程ID")
    private String courseId;

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "狀態")
    private EnrollmentStatus status;

    @Schema(description = "報名原因")
    private String reason;

    @Schema(description = "備註")
    private String remarks;

    @Schema(description = "審核人")
    private String approvedBy;

    @Schema(description = "審核時間")
    private LocalDateTime approvedAt;

    @Schema(description = "拒絕人")
    private String rejectedBy;

    @Schema(description = "拒絕時間")
    private LocalDateTime rejectedAt;

    @Schema(description = "拒絕原因")
    private String rejectReason;

    @Schema(description = "取消人")
    private String cancelledBy;

    @Schema(description = "取消時間")
    private LocalDateTime cancelledAt;

    @Schema(description = "取消原因")
    private String cancelReason;

    @Schema(description = "是否出席")
    private boolean attendance;

    @Schema(description = "出席時數")
    private BigDecimal attendedHours;

    @Schema(description = "完成時數")
    private BigDecimal completedHours;

    @Schema(description = "成績")
    private BigDecimal score;

    @Schema(description = "是否通過")
    private Boolean passed;

    @Schema(description = "評語")
    private String feedback;

    @Schema(description = "完成時間")
    private LocalDateTime completedAt;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}
