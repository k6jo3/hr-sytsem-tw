package com.company.hrms.document.application.service.generate.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.generate.context.GenerateDocumentContext;
import com.company.hrms.document.domain.model.Document;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存文件紀錄任務
 */
@Component
@RequiredArgsConstructor
public class SaveDocumentTask implements PipelineTask<GenerateDocumentContext> {

    private final IDocumentRepository documentRepository;

    @Override
    public void execute(GenerateDocumentContext context) {
        var req = context.getRequest();

        // 建立文件領域對象
        DocumentId id = new DocumentId(UUID.randomUUID().toString());
        Document doc = Document.create(id, context.getFileName(), req.getEmployeeId());

        // 設定屬性
        doc.completeUpload(
                context.getGeneratedFilePath(),
                context.getMimeType(),
                context.getFileSize());

        // 系統產生文件預設通常為個人私有或受控
        // 根據需求設定 Visibility 和 Classification
        // doc.setVisibility(...)
        // doc.setClassification(...)

        // 儲存
        Document savedDoc = documentRepository.save(doc);
        context.setDocument(savedDoc);
    }
}
