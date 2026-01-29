package com.company.hrms.document.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;

/**
 * 文件存取紀錄實體
 */
@Getter
public class DocumentAccessLog extends com.company.hrms.common.domain.model.Entity<DocumentAccessLogId> {

    private final String documentId;
    private final String userId;
    private final String action; // DOWNLOAD, VIEW, DELETE
    private final String ipAddress;
    private final LocalDateTime accessedAt;

    public DocumentAccessLog(DocumentAccessLogId id, String documentId, String userId, String action,
            String ipAddress) {
        super(id);
        this.documentId = documentId;
        this.userId = userId;
        this.action = action;
        this.ipAddress = ipAddress;
        this.accessedAt = LocalDateTime.now();
    }

    public static DocumentAccessLog create(String documentId, String userId, String action, String ipAddress) {
        return new DocumentAccessLog(new DocumentAccessLogId(java.util.UUID.randomUUID().toString()), documentId,
                userId, action, ipAddress);
    }
}
