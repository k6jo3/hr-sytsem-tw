package com.company.hrms.document.domain.model;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;

/**
 * 文件範本 Repository 接口
 */
public interface IDocumentTemplateRepository {

    DocumentTemplate save(DocumentTemplate template);

    Optional<DocumentTemplate> findById(DocumentTemplateId id);

    Optional<DocumentTemplate> findByCode(String code);

    Page<DocumentTemplate> findTemplates(QueryGroup query, Pageable pageable);
}
