package com.company.hrms.document.application.service.upload.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.document.application.service.upload.context.UploadDocumentContext;
import com.company.hrms.document.domain.event.DocumentUploadedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PublishEventTask implements PipelineTask<UploadDocumentContext> {

    private final EventPublisher eventPublisher;

    @Override
    public void execute(UploadDocumentContext context) {
        var doc = context.getDocument();
        if (doc != null) {
            DocumentUploadedEvent event = new DocumentUploadedEvent(
                    doc.getId().getValue(),
                    doc.getOwnerId(),
                    doc.getFileName(),
                    doc.getUploadedAt());

            eventPublisher.publish(event);
        }
    }
}
