package com.company.hrms.document.infrastructure.persistence.po;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 文件申請持久化物件
 */
@Data
@Entity
@Table(name = "hrs_document_requests")
public class DocumentRequestPO {
    @Id
    @Column(name = "request_id")
    private String requestId;

    @Column(name = "template_code")
    private String templateCode;

    @Column(name = "requester_id")
    private String requesterId;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "status")
    private String status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "document_id")
    private String documentId;
}
