package com.company.hrms.document.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.domain.model.DocumentAccessLog;
import com.company.hrms.document.domain.model.DocumentAccessLogId;
import com.company.hrms.document.domain.model.IDocumentAccessLogRepository;
import com.company.hrms.document.infrastructure.persistence.po.DocumentAccessLogPO;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class DocumentAccessLogRepositoryImpl extends BaseRepository<DocumentAccessLogPO, String>
        implements IDocumentAccessLogRepository {

    public DocumentAccessLogRepositoryImpl(JPAQueryFactory factory) {
        super(factory, DocumentAccessLogPO.class);
    }

    @Override
    public void save(DocumentAccessLog log) {
        DocumentAccessLogPO po = new DocumentAccessLogPO();
        po.setLogId(log.getId().getValue());
        po.setDocumentId(log.getDocumentId());
        po.setUserId(log.getUserId());
        po.setAction(log.getAction());
        po.setIpAddress(log.getIpAddress());
        po.setAccessedAt(log.getAccessedAt());
        super.save(po);
    }

    @Override
    public Page<DocumentAccessLog> findLogs(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(po -> new DocumentAccessLog(new DocumentAccessLogId(po.getLogId()),
                po.getDocumentId(), po.getUserId(), po.getAction(), po.getIpAddress()));
    }
}
