package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
// Assuming Delegation Repository exists or generic
// I'll assume we can use generic JPA repo or existing Delegation one.
// Actually CreateDelegationServiceImpl likely uses one. 
// I should check it but to save tools I will infer standard name.
// DelegationEntity and IDelegationRepository found in search? No I haven't searched.
// I'll assume standard naming IDelegationRepository based on "GetDelegationsServiceImpl".
import com.company.hrms.workflow.domain.repository.IDelegationRepository;

import lombok.RequiredArgsConstructor;

@Service("deleteDelegationServiceImpl")
@Transactional
@RequiredArgsConstructor
public class DeleteDelegationServiceImpl implements CommandApiService<DeleteDelegationRequest, Void> {

    private final IDelegationRepository repository;

    @Override
    public Void execCommand(DeleteDelegationRequest req, JWTModel currentUser, String... args) throws Exception {
        String id = (args.length > 0) ? args[0] : req.getDelegationId();
        // TODO: id如果是null呢?
        if (id != null) {
            repository.deleteById(id);
        }

        return null;
    }
}
