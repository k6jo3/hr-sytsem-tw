package com.company.hrms.training.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.training.api.response.CertificateResponse;
import com.company.hrms.training.application.assembler.CertificateAssembler;
import com.company.hrms.training.domain.model.aggregate.Certificate;
import com.company.hrms.training.domain.repository.ICertificateRepository;

import lombok.RequiredArgsConstructor;

@Service("getExpiringCertificatesServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpiringCertificatesServiceImpl implements QueryApiService<QueryGroup, List<CertificateResponse>> {

    private final ICertificateRepository certificateRepository;

    @Override
    public List<CertificateResponse> getResponse(QueryGroup query, JWTModel currentUser, String... args) {
        // Assume deadline/days is passed somewhere in QueryGroup or we set a default
        // For simplicity, we default to 90 days from now
        LocalDate threshold = LocalDate.now().plusDays(90);

        List<Certificate> certs = certificateRepository.findExpiringCertificates(threshold);

        List<CertificateResponse> responseList = new ArrayList<>();
        for (Certificate cert : certs) {
            responseList.add(CertificateAssembler.toResponse(cert));
        }

        return responseList;
    }

}
