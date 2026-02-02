package com.company.hrms.training.application.service.task.certificate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.CertificateContext;
import com.company.hrms.training.domain.repository.ICertificateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存證照 Task (通用)
 */
@Component
@RequiredArgsConstructor
public class SaveCertificateActionTask<C extends CertificateContext> implements PipelineTask<C> {

    private final ICertificateRepository certificateRepository;

    @Override
    public void execute(C context) {
        certificateRepository.save(context.getCertificate());
    }
}
