package com.company.hrms.training.application.task.certificate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.CertificateIdContext;
import com.company.hrms.training.domain.model.aggregate.Certificate;
import com.company.hrms.training.domain.model.valueobject.CertificateId;
import com.company.hrms.training.domain.repository.ICertificateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入證照 Task (通用)
 */
@Component
@RequiredArgsConstructor
public class LoadCertificateActionTask<C extends CertificateIdContext> implements PipelineTask<C> {

    private final ICertificateRepository certificateRepository;

    @Override
    public void execute(C context) {
        Certificate cert = certificateRepository.findById(CertificateId.from(context.getCertificateId()))
                .orElseThrow(
                        () -> new IllegalArgumentException("Certificate not found: " + context.getCertificateId()));
        context.setCertificate(cert);
    }
}
