package com.company.hrms.document.application.service.delete.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.document.api.request.DeleteDocumentRequest;
import com.company.hrms.document.domain.model.Document;

import lombok.Getter;
import lombok.Setter;

/**
 * 刪除文件 Pipeline 上下文
 */
@Getter
@Setter
public class DeleteDocumentContext extends PipelineContext {
    private final DeleteDocumentRequest request;
    private Document document;

    public DeleteDocumentContext(DeleteDocumentRequest request) {
        this.request = request;
    }
}
