package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 換班申請持久化物件
 */
@Entity
@Table(name = "shift_swap_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftSwapRequestPO {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "requester_id", length = 50, nullable = false)
    private String requesterId;

    @Column(name = "counterpart_id", length = 50, nullable = false)
    private String counterpartId;

    @Column(name = "requester_date", nullable = false)
    private LocalDate requesterDate;

    @Column(name = "counterpart_date", nullable = false)
    private LocalDate counterpartDate;

    @Column(name = "requester_shift_id", length = 50)
    private String requesterShiftId;

    @Column(name = "counterpart_shift_id", length = 50)
    private String counterpartShiftId;

    @Column(name = "status", length = 30, nullable = false)
    private String status;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "approver_id", length = 50)
    private String approverId;

    @Column(name = "is_deleted")
    private Integer isDeleted;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}
