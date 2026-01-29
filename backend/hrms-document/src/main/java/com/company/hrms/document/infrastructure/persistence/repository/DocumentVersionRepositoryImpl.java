package com.company.hrms.document.infrastructure.persistence.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.document.domain.model.DocumentVersion;
import com.company.hrms.document.domain.model.IDocumentVersionRepository;
import com.company.hrms.document.infrastructure.persistence.po.DocumentVersionPO;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 文件版本 Repository 實作
 * 使用 PathBuilder 避免編譯時對 Q 類別的靜態依賴問題
 */
@Repository
public class DocumentVersionRepositoryImpl extends BaseRepository<DocumentVersionPO, String>
        implements IDocumentVersionRepository {

    public DocumentVersionRepositoryImpl(JPAQueryFactory factory) {
        super(factory, DocumentVersionPO.class);
    }

    @Override
    public void save(DocumentVersion domain) {
        DocumentVersionPO po = new DocumentVersionPO();
        po.setVersionId(domain.getId().getValue());
        po.setDocumentId(domain.getDocumentId());
        po.setVersionNumber(domain.getVersion());
        po.setFileName(domain.getFileName());
        po.setFileSize(domain.getFileSize());
        po.setStoragePath(domain.getStoragePath());
        po.setUploaderId(domain.getUploaderId());
        po.setUploadedAt(domain.getUploadedAt());
        po.setChangeNote(domain.getChangeNote());
        super.save(po);
    }

    @Override
    public List<DocumentVersion> findByDocumentId(String documentId) {
        PathBuilder<DocumentVersionPO> path = new PathBuilder<>(DocumentVersionPO.class, "documentVersionPO");
        return factory.selectFrom(path)
                .where(path.get("documentId", String.class).eq(documentId))
                .orderBy(path.getNumber("versionNumber", Integer.class).desc())
                .fetch()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private DocumentVersion toDomain(DocumentVersionPO po) {
        return new DocumentVersion(
                new DocumentVersion.DocumentVersionId(po.getVersionId()),
                po.getDocumentId(),
                po.getVersionNumber(),
                po.getFileName(),
                po.getFileSize(),
                po.getStoragePath(),
                po.getUploaderId(),
                po.getChangeNote());
    }
}
