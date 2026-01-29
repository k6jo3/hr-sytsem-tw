package com.company.hrms.document.application.service.delete.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.delete.context.DeleteDocumentContext;
import com.company.hrms.document.domain.model.IDocumentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 執行軟刪除任務
 */
@Component
@RequiredArgsConstructor
public class ExecuteDeleteTask implements PipelineTask<DeleteDocumentContext> {

    private final IDocumentRepository documentRepository;

    @Override
    public void execute(DeleteDocumentContext context) {
        var doc = context.getDocument();

        // 執行軟刪除
        doc.markAsDeleted();

        // 儲存
        documentRepository.save(doc);
    }
}
