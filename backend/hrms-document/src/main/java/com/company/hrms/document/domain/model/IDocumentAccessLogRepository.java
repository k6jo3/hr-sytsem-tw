package com.company.hrms.document.domain.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;

/**
 * 文件存取紀錄 Repository 介面
 */
public interface IDocumentAccessLogRepository {
    void save(DocumentAccessLog log);

    Page<DocumentAccessLog> findLogs(QueryGroup query, Pageable pageable);
}
