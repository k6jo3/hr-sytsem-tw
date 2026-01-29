package com.company.hrms.document.domain.model;

import java.util.List;

/**
 * 文件版本 Repository 介面
 */
public interface IDocumentVersionRepository {
    void save(DocumentVersion version);

    List<DocumentVersion> findByDocumentId(String documentId);
}
