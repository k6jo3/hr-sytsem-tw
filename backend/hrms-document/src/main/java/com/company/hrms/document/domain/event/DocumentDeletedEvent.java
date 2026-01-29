package com.company.hrms.document.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 文件已刪除事件
 */
@Getter
@NoArgsConstructor
public class DocumentDeletedEvent extends DomainEvent {
    private String documentId;
    private String ownerId;
    private LocalDateTime deletedAt;

    public DocumentDeletedEvent(String documentId, String ownerId, LocalDateTime deletedAt) {
        super();
        this.documentId = documentId;
        this.ownerId = ownerId;
        this.deletedAt = deletedAt;
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
