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

        // 模擬變數替換邏輯
        String content = template.getContent();
        if (content != null) {
            for (java.util.Map.Entry<String, Object> entry : dataModel.entrySet()) {
                String key = "{{" + entry.getKey() + "}}";
                String value = String.valueOf(entry.getValue());
                content = content.replace(key, value);
            }
        }

        String generatedFileName = template.getName() + "_" + dataModel.getOrDefault("employeeName", "Unknown") + "_"
                + System.currentTimeMillis() + ".pdf";
        String storageId = UUID.randomUUID().toString();
        String storagePath = "/shared/documents/generated/" + storageId + ".pdf";

        context.setFileName(generatedFileName);
        context.setGeneratedFilePath(storagePath);
        context.setMimeType("application/pdf");

        // 模擬生成檔案大小
        byte[] finalContent = (content != null ? content : "PDF Mock Content").getBytes();
        context.setFileSize((long) finalContent.length);

        log.info("Document rendered successfully: {}, size: {} bytes", generatedFileName, finalContent.length);
    }
}
