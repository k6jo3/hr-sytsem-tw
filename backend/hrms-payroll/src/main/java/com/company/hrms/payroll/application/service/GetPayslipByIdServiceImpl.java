package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.response.PayslipResponse;
import com.company.hrms.payroll.application.factory.PayslipDtoFactory;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.valueobject.PayslipId;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;

import lombok.RequiredArgsConstructor;

/**
 * 依 ID 查詢薪資單服務
 * 
 * <p>
 * <b>Business Pipeline 說明：</b>
 * </p>
 * <p>
 * 依據 {@code 03_Business_Pipeline.md} 架構文件，Business Pipeline 模式
 * 適用於「複雜的多步驟業務流程編排」。
 * </p>
 * 
 * <p>
 * 本服務為<b>簡單 ID 查詢服務</b>，僅執行：
 * </p>
 * <ol>
 * <li>接收 ID 參數</li>
 * <li>呼叫 Repository.findById()</li>
 * <li>轉換為 Response DTO</li>
 * </ol>
 * 
 * <p>
 * 此流程單純、無副作用、無跨服務協調需求，因此<b>不需使用 Business Pipeline 模式</b>，
 * 直接使用 {@link com.company.hrms.common.application.service.AbstractQueryService}
 * 提供的 {@code buildQuery/executeQuery} 樣板方法即可。
 * </p>
 */
@Service("getPayslipByIdServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPayslipByIdServiceImpl extends AbstractQueryService<String, PayslipResponse> {

    private final IPayslipRepository repository;

    @Override
    protected QueryGroup buildQuery(String request, JWTModel currentUser) {
        return QueryBuilder.where().build();
    }

    @Override
    protected PayslipResponse executeQuery(QueryGroup query, String id, JWTModel currentUser, String... args)
            throws Exception {
        String targetId = (args != null && args.length > 0) ? args[0] : id;

        if (targetId == null) {
            throw new IllegalArgumentException("ID 為必填");
        }

        Payslip payslip = repository.findById(new PayslipId(targetId))
                .orElseThrow(() -> new DomainException("PAYSLIP_NOT_FOUND", "找不到薪資單: " + targetId));

        return PayslipDtoFactory.toResponse(payslip);
    }
}
