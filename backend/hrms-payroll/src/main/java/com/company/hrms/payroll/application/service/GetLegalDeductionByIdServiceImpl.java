package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.response.LegalDeductionResponse;
import com.company.hrms.payroll.application.factory.LegalDeductionDtoFactory;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 依 ID 查詢法扣款服務
 *
 * 本服務為簡單 ID 查詢服務，不需使用 Business Pipeline 模式，
 * 直接使用 AbstractQueryService 提供的樣板方法即可。
 */
@Service("getLegalDeductionByIdServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetLegalDeductionByIdServiceImpl extends AbstractQueryService<String, LegalDeductionResponse> {

    private final ILegalDeductionRepository repository;

    @Override
    protected QueryGroup buildQuery(String request, JWTModel currentUser) {
        return QueryBuilder.where().build();
    }

    @Override
    protected LegalDeductionResponse executeQuery(QueryGroup query, String id, JWTModel currentUser, String... args)
            throws Exception {
        String targetId = (args != null && args.length > 0) ? args[0] : id;

        if (targetId == null) {
            throw new IllegalArgumentException("法扣款 ID 為必填");
        }

        LegalDeduction deduction = repository.findById(new DeductionId(targetId))
                .orElseThrow(() -> new DomainException("LEGAL_DEDUCTION_NOT_FOUND",
                        "找不到法扣款: " + targetId));

        return LegalDeductionDtoFactory.toResponse(deduction);
    }
}
