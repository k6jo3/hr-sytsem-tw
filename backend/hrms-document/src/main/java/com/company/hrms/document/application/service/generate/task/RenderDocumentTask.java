package com.company.hrms.document.application.service.generate.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.generate.context.GenerateDocumentContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 渲染文件任務 (由範本產生 PDF)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RenderDocumentTask implements PipelineTask<GenerateDocumentContext> {

    @Override
    public void execute(GenerateDocumentContext context) {
        var template = context.getTemplate();
        var dataModel = context.getDataModel();

        log.info("Rendering document using template: {} with data: {}", template.getCode(), dataModel);

        // TODO: 具體實作應使用 Apache POI (Word) 或 Thymeleaf/iText (HTML/PDF) 產生真實檔案
        // 目前模擬產生過程
        String generatedFileName = template.getName() + "_" + dataModel.get("employeeName") + "_"
                + System.currentTimeMillis() + ".pdf";
        String storagePath = "/storage/generated/" + UUID.randomUUID().toString() + ".pdf";

        context.setFileName(generatedFileName);
        context.setGeneratedFilePath(storagePath);
        context.setMimeType("application/pdf");
        context.setFileSize(102400L); // 模擬 100KB
    }
}
