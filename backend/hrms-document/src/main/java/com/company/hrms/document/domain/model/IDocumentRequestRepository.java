package com.company.hrms.document.domain.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;

public interface IDocumentRequestRepository {
    void save(DocumentRequest request);

    Page<DocumentRequest> findRequests(QueryGroup query, Pageable pageable);
}
