package com.company.hrms.notification.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公告閱讀記錄持久化物件
 */
@Entity
@Table(name = "announcement_read_records", indexes = {
        @Index(name = "idx_announcement_employee", columnList = "announcement_id, employee_id")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = { "announcement_id", "employee_id" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementReadRecordPO {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "announcement_id", length = 50, nullable = false)
    private String announcementId;

    @Column(name = "employee_id", length = 50, nullable = false)
    private String employeeId;

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}
