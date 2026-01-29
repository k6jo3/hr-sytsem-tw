package com.company.hrms.document.infrastructure.persistence.po;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "hrs_document_access_logs")
public class DocumentAccessLogPO {
    @Id
    @Column(name = "log_id")
    private String logId;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "action")
    private String action;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "accessed_at")
    private LocalDateTime accessedAt;
}
