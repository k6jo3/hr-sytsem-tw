package com.company.hrms.document.application.service.delete.task;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.delete.context.DeleteDocumentContext;
import com.company.hrms.document.domain.event.DocumentDeletedEvent;

import lombok.RequiredArgsConstructor;

/**
 * 發布刪除事件任務
 */
@Component("deletePublishEventTask")
@RequiredArgsConstructor
public class PublishDeleteEventTask implements PipelineTask<DeleteDocumentContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(DeleteDocumentContext context) {
        var doc = context.getDocument();

        DocumentDeletedEvent event = new DocumentDeletedEvent(
                doc.getId().getValue(),
                doc.getOwnerId(),
                LocalDateTime.now());

        eventPublisher.publishEvent(event);
    }
}
