package com.company.hrms.document.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DocumentUploadedEvent extends DomainEvent {
    private String documentId;
    private String ownerId;
    private String fileName;
    private LocalDateTime uploadedAt;

    public DocumentUploadedEvent(String documentId, String ownerId, String fileName, LocalDateTime uploadedAt) {
        super();
        this.documentId = documentId;
        this.ownerId = ownerId;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
    }

    @Override
    public String getAggregateId() {
        return documentId;
    }

    @Override
    public String getAggregateType() {
        return "Document";
    }
}
