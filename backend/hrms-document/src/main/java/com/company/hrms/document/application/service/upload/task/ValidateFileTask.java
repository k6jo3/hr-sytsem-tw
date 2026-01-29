package com.company.hrms.document.application.service.upload.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.upload.context.UploadDocumentContext;

@Component
public class ValidateFileTask implements PipelineTask<UploadDocumentContext> {

    @Override
    public void execute(UploadDocumentContext context) {
        var req = context.getRequest();
        if (req.getFileContent() == null || req.getFileContent().length == 0) {
            throw new IllegalArgumentException("File content is empty");
        }
        if (req.getFileName() == null || req.getFileName().isBlank()) {
            throw new IllegalArgumentException("File name is missing");
        }
        // Add more validation like size limit, etc.
    }
}
