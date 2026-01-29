package com.company.hrms.document.application.service.upload.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.upload.context.UploadDocumentContext;
import com.company.hrms.document.domain.model.Document;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaveDatabaseTask implements PipelineTask<UploadDocumentContext> {

    private final IDocumentRepository repository;

    @Override
    public void execute(UploadDocumentContext context) {
        var req = context.getRequest();

        DocumentId id = new DocumentId(UUID.randomUUID().toString());
        Document doc = Document.create(id, req.getFileName(), req.getOwnerId());

        doc.completeUpload(context.getSavedStoragePath(), req.getMimeType(), req.getFileSize());

        // Enrich context with created document
        context.setDocument(repository.save(doc));
    }
}
