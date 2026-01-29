package com.company.hrms.document.domain.model;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.common.domain.model.Identifier;

import lombok.Getter;

@Getter
public class DocumentRequest extends AggregateRoot<DocumentRequest.DocumentRequestId> {

    private final String templateCode;
    private final String requesterId;
    private final String purpose;
    private final String status; // PENDING, APPROVED, REJECTED, COMPLETED
    private final LocalDateTime requestedAt;
    private String documentId;

    public DocumentRequest(DocumentRequestId id, String templateCode, String requesterId, String purpose,
            String status) {
        super(id);
        this.templateCode = templateCode;
        this.requesterId = requesterId;
        this.purpose = purpose;
        this.status = status;
        this.requestedAt = LocalDateTime.now();
    }

    public static DocumentRequest create(String templateCode, String requesterId, String purpose) {
        return new DocumentRequest(new DocumentRequestId(java.util.UUID.randomUUID().toString()), templateCode,
                requesterId, purpose, "PENDING");
    }

    public void complete(String documentId) {
        this.documentId = documentId;
        // 這裡可以切換狀態
    }

    @Getter
    public static class DocumentRequestId extends Identifier<String> {
        public DocumentRequestId(String value) {
            super(value);
        }
    }
}
