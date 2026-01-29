package com.company.hrms.document.domain.model;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;

public interface IDocumentRepository {
    Document save(Document document);

    Optional<Document> findById(DocumentId id);

    Page<Document> findDocuments(QueryGroup query, Pageable pageable);
}
