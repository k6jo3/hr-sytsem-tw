package com.company.hrms.document.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.domain.model.DocumentRequest;
import com.company.hrms.document.domain.model.IDocumentRequestRepository;
import com.company.hrms.document.infrastructure.persistence.po.DocumentRequestPO;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 文件申請 Repository 實作
 */
@Repository
public class DocumentRequestRepositoryImpl extends BaseRepository<DocumentRequestPO, String>
        implements IDocumentRequestRepository {

    public DocumentRequestRepositoryImpl(JPAQueryFactory factory) {
        super(factory, DocumentRequestPO.class);
    }

    @Override
    public void save(DocumentRequest domain) {
        DocumentRequestPO po = new DocumentRequestPO();
        po.setRequestId(domain.getId().getValue());
        po.setTemplateCode(domain.getTemplateCode());
        po.setRequesterId(domain.getRequesterId());
        po.setPurpose(domain.getPurpose());
        po.setStatus(domain.getStatus());
        po.setRequestedAt(domain.getRequestedAt());
        po.setDocumentId(domain.getDocumentId());
        super.save(po);
    }

    @Override
    public Page<DocumentRequest> findRequests(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    private DocumentRequest toDomain(DocumentRequestPO po) {
        // 使用建構子或 reconstitute 模式
        DocumentRequest domain = new DocumentRequest(
                new DocumentRequest.DocumentRequestId(po.getRequestId()),
                po.getTemplateCode(),
                po.getRequesterId(),
                po.getPurpose(),
                po.getStatus());
        if (po.getDocumentId() != null) {
            domain.complete(po.getDocumentId());
        }
        return domain;
    }
}
