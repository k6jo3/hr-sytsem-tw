package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.response.SalaryAdvanceResponse;
import com.company.hrms.payroll.application.factory.SalaryAdvanceDtoFactory;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.repository.ISalaryAdvanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * 依 ID 查詢預借薪資服務
 *
 * <p>
 * 本服務為簡單 ID 查詢，僅執行：
 * 1. 接收 ID 參數
 * 2. 呼叫 Repository.findById()
 * 3. 轉換為 Response DTO
 * 不需使用 Business Pipeline 模式。
 * </p>
 */
@Service("getSalaryAdvanceByIdServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSalaryAdvanceByIdServiceImpl extends AbstractQueryService<String, SalaryAdvanceResponse> {

    private final ISalaryAdvanceRepository repository;

    @Override
    protected QueryGroup buildQuery(String request, JWTModel currentUser) {
        return QueryBuilder.where().build();
    }

    @Override
    protected SalaryAdvanceResponse executeQuery(QueryGroup query, String request, JWTModel currentUser,
            String... args) throws Exception {
        String targetId = (args != null && args.length > 0) ? args[0] : request;

        if (targetId == null || targetId.isBlank()) {
            throw new IllegalArgumentException("預借薪資 ID 為必填");
        }

        SalaryAdvance advance = repository.findById(new AdvanceId(targetId))
                .orElseThrow(() -> new DomainException(
                        "SALARY_ADVANCE_NOT_FOUND",
                        "找不到預借薪資記錄: " + targetId));

        return SalaryAdvanceDtoFactory.toResponse(advance);
    }
}
