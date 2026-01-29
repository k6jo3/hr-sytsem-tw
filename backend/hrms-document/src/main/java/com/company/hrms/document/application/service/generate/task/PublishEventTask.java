package com.company.hrms.document.application.service.generate.task;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.generate.context.GenerateDocumentContext;
import com.company.hrms.document.domain.event.DocumentGeneratedEvent;

import lombok.RequiredArgsConstructor;

/**
 * 發布文件產生事件任務
 */
@Component("generatePublishEventTask")
@RequiredArgsConstructor
public class PublishEventTask implements PipelineTask<GenerateDocumentContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(GenerateDocumentContext context) {
        var doc = context.getDocument();
        var template = context.getTemplate();

        DocumentGeneratedEvent event = new DocumentGeneratedEvent(
                doc.getId().getValue(),
                template.getId().getValue(),
                doc.getOwnerId(),
                LocalDateTime.now());

        eventPublisher.publishEvent(event);
    }
}
