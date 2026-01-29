package com.company.hrms.document.application.service.upload.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.upload.context.UploadDocumentContext;

@Component
public class SaveStorageTask implements PipelineTask<UploadDocumentContext> {

    @Override
    public void execute(UploadDocumentContext context) {
        // Mock storage
        String storagePath = "s3://bucket/docs/" + UUID.randomUUID() + "/" + context.getRequest().getFileName();
        context.setSavedStoragePath(storagePath);
        // Real implementation would use S3 client or File IO
    }
}
