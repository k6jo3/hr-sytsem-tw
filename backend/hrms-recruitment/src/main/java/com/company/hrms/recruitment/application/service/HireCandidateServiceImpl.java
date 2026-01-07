package com.company.hrms.recruitment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.service.context.HireCandidateContext;
import com.company.hrms.recruitment.application.service.task.HireCandidateTask;
import com.company.hrms.recruitment.application.service.task.LoadCandidateTask;
import com.company.hrms.recruitment.application.service.task.LoadOfferByCandidateTask;
import com.company.hrms.recruitment.application.service.task.ValidateOfferAcceptedTask;

import lombok.RequiredArgsConstructor;

/**
 * 錄取應徵者 Service
 */
@Service("hireCandidateServiceImpl")
@RequiredArgsConstructor
public class HireCandidateServiceImpl implements CommandApiService<String, Void> {

    private final LoadCandidateTask loadCandidateTask;
    private final LoadOfferByCandidateTask loadOfferByCandidateTask;
    private final ValidateOfferAcceptedTask validateOfferAcceptedTask;
    private final HireCandidateTask hireCandidateTask;

    @Override
    @Transactional
    public Void execCommand(String candidateId, Object currentUser, String... args) throws Exception {
        BusinessPipeline.start(new HireCandidateContext(candidateId))
                .next(loadCandidateTask)
                .next(loadOfferByCandidateTask)
                .next(validateOfferAcceptedTask)
                .next(hireCandidateTask)
                .execute();

        return null;
    }
}
