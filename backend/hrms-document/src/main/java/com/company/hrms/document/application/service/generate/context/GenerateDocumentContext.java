package com.company.hrms.document.application.service.generate.context;

import java.util.Map;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.document.api.request.GenerateDocumentRequest;
import com.company.hrms.document.domain.model.Document;
import com.company.hrms.document.domain.model.DocumentTemplate;

import lombok.Getter;
import lombok.Setter;

/**
 * 產生文件 Pipeline 上下文
 */
@Getter
@Setter
public class GenerateDocumentContext extends PipelineContext {

    private final GenerateDocumentRequest request;

    // 中間數據
    private DocumentTemplate template;
    private Map<String, Object> dataModel;
    private String generatedFilePath;
    private String fileName;
    private String mimeType = "application/pdf";
    private long fileSize;

    // 輸出
    private Document document;

    public GenerateDocumentContext(GenerateDocumentRequest request) {
        this.request = request;
    }
}
