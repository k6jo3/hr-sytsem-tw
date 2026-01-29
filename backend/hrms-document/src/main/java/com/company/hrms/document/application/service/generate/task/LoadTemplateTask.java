package com.company.hrms.document.application.service.generate.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.generate.context.GenerateDocumentContext;
import com.company.hrms.document.domain.model.IDocumentTemplateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入範本任務
 */
@Component
@RequiredArgsConstructor
public class LoadTemplateTask implements PipelineTask<GenerateDocumentContext> {

    private final IDocumentTemplateRepository templateRepository;

    @Override
    public void execute(GenerateDocumentContext context) {
        String templateCode = context.getRequest().getTemplateCode();

        var template = templateRepository.findByCode(templateCode)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateCode));

        if (template.getStatus() != com.company.hrms.document.domain.model.enums.DocumentTemplateStatus.ACTIVE) {
            throw new IllegalStateException("Template is not active: " + templateCode);
        }

        context.setTemplate(template);
    }
}
