package com.company.hrms.document.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.domain.model.Document;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentRepository;
import com.company.hrms.document.infrastructure.persistence.mapper.DocumentMapper;
import com.company.hrms.document.infrastructure.persistence.po.DocumentPO;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class DocumentRepositoryImpl extends BaseRepository<DocumentPO, String> implements IDocumentRepository {

    public DocumentRepositoryImpl(JPAQueryFactory factory) {
        super(factory, DocumentPO.class);
    }

    @Override
    public Document save(Document document) {
        DocumentPO po = DocumentMapper.toPO(document);
        DocumentPO savedPO = super.save(po);
        return DocumentMapper.toDomain(savedPO);
    }

    @Override
    public Optional<Document> findById(DocumentId id) {
        return super.findById(id.getValue())
                .map(DocumentMapper::toDomain);
    }

    @Override
    public Page<Document> findDocuments(QueryGroup query, Pageable pageable) {
        Page<DocumentPO> page = super.findPage(query, pageable);
        return page.map(DocumentMapper::toDomain);
    }
}
