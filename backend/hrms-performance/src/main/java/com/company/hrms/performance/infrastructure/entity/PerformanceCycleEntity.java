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

/**
 * 考核週期 Entity
 */
@Entity
@Table(name = "performance_cycles")
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

    // === Getters and Setters (Manual due to Lombok issues) ===

    public UUID getCycleId() {
        return cycleId;
    }

    public void setCycleId(UUID cycleId) {
        this.cycleId = cycleId;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public CycleType getCycleType() {
        return cycleType;
    }

    public void setCycleType(CycleType cycleType) {
        this.cycleType = cycleType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getSelfEvalDeadline() {
        return selfEvalDeadline;
    }

    public void setSelfEvalDeadline(LocalDate selfEvalDeadline) {
        this.selfEvalDeadline = selfEvalDeadline;
    }

    public LocalDate getManagerEvalDeadline() {
        return managerEvalDeadline;
    }

    public void setManagerEvalDeadline(LocalDate managerEvalDeadline) {
        this.managerEvalDeadline = managerEvalDeadline;
    }

    public CycleStatus getStatus() {
        return status;
    }

    public void setStatus(CycleStatus status) {
        this.status = status;
    }

    public String getTemplateJson() {
        return templateJson;
    }

    public void setTemplateJson(String templateJson) {
        this.templateJson = templateJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
