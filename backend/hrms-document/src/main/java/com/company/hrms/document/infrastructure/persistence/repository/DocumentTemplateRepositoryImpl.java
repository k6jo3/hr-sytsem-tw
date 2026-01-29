package com.company.hrms.document.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.domain.model.DocumentTemplate;
import com.company.hrms.document.domain.model.DocumentTemplateId;
import com.company.hrms.document.domain.model.IDocumentTemplateRepository;
import com.company.hrms.document.infrastructure.persistence.mapper.DocumentTemplateEntityMapper;
import com.company.hrms.document.infrastructure.persistence.po.DocumentTemplatePO;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 文件範本 Repository 實作
 */
@Repository
public class DocumentTemplateRepositoryImpl extends BaseRepository<DocumentTemplatePO, String>
        implements IDocumentTemplateRepository {

    public DocumentTemplateRepositoryImpl(JPAQueryFactory factory) {
        super(factory, DocumentTemplatePO.class);
    }

    @Override
    public DocumentTemplate save(DocumentTemplate template) {
        DocumentTemplatePO po = DocumentTemplateEntityMapper.toPO(template);
        DocumentTemplatePO savedPO = super.save(po);
        return DocumentTemplateEntityMapper.toDomain(savedPO);
    }

    @Override
    public Optional<DocumentTemplate> findById(DocumentTemplateId id) {
        return super.findById(id.getValue())
                .map(DocumentTemplateEntityMapper::toDomain);
    }

    @Override
    public Optional<DocumentTemplate> findByCode(String code) {
        com.company.hrms.common.query.QueryGroup query = com.company.hrms.common.query.QueryGroup.and().eq("code",
                code);

        return super.findPage(query, org.springframework.data.domain.PageRequest.of(0, 1))
                .getContent().stream()
                .findFirst()
                .map(DocumentTemplateEntityMapper::toDomain);
    }

    @Override
    public Page<DocumentTemplate> findTemplates(QueryGroup query, Pageable pageable) {
        Page<DocumentTemplatePO> page = super.findPage(query, pageable);
        return page.map(DocumentTemplateEntityMapper::toDomain);
    }
}
