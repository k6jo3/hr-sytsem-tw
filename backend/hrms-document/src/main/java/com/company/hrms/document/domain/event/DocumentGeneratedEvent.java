package com.company.hrms.document.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DocumentGeneratedEvent extends DomainEvent {
    private String documentId;
    private String templateId;
    private String ownerId;
    private LocalDateTime generatedAt;

    public DocumentGeneratedEvent(String documentId, String templateId, String ownerId, LocalDateTime generatedAt) {
        super();
        this.documentId = documentId;
        this.templateId = templateId;
        this.ownerId = ownerId;
        this.generatedAt = generatedAt;
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
