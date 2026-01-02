package com.company.hrms.performance.infrastructure.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.performance.domain.model.valueobject.CycleStatus;
import com.company.hrms.performance.domain.model.valueobject.CycleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 考核週期 Entity
 */
@Entity
@Table(name = "performance_cycles")
@Data
public class PerformanceCycleEntity {

    @Id
    @Column(name = "cycle_id")
    private UUID cycleId;

    @Column(name = "cycle_name", nullable = false, length = 100)
    private String cycleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "cycle_type", nullable = false, length = 20)
    private CycleType cycleType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "self_eval_deadline")
    private LocalDate selfEvalDeadline;

    @Column(name = "manager_eval_deadline")
    private LocalDate managerEvalDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CycleStatus status;

    // 考核表單範本（JSONB 儲存為 String）
    @Column(name = "template", columnDefinition = "TEXT")
    private String templateJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
