package com.company.hrms.document.application.service.upload.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.document.api.request.UploadDocumentRequest;
import com.company.hrms.document.domain.model.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadDocumentContext extends PipelineContext {
    private final UploadDocumentRequest request;
    private Document document;

    // Intermediate results
    private boolean isVirusFree;
    private String savedStoragePath;

    public UploadDocumentContext(UploadDocumentRequest request) {
        this.request = request;
    }
}
