package com.company.hrms.document.application.service.delete.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.delete.context.DeleteDocumentContext;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入文件任務
 */
@Component("deleteLoadDocumentTask")
@RequiredArgsConstructor
public class LoadDocumentTask implements PipelineTask<DeleteDocumentContext> {

    private final IDocumentRepository documentRepository;

    @Override
    public void execute(DeleteDocumentContext context) {
        String docId = context.getRequest().getDocumentId();
        var document = documentRepository.findById(new DocumentId(docId))
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + docId));

        context.setDocument(document);
    }
}
