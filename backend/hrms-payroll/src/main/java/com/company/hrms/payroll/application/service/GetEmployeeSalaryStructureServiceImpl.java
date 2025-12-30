package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.payroll.application.dto.response.SalaryStructureResponse;
import com.company.hrms.payroll.application.factory.SalaryStructureDtoFactory;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.repository.ISalaryStructureRepository;

import lombok.RequiredArgsConstructor;

/**
 * 依員工 ID 查詢薪資結構服務
 *
 * <h3>重構說明</h3>
 * <p>
 * 本服務為<b>簡單單筆查詢服務</b>，直接使用 Repository 提供的
 * {@code findByEmployeeId()} 方法取得最新生效的薪資結構。
 * </p>
 *
 * <h4>重構前：</h4>
 * <pre>
 * QueryGroup filter = QueryBuilder.where()
 *         .and("employeeId", Operator.EQ, targetEmpId)
 *         .and("active", Operator.EQ, true)
 *         .build();
 * List&lt;SalaryStructure&gt; structures = repository
 *         .findAll(filter, PageRequest.of(0, 1)).getContent();
 * </pre>
 *
 * <h4>重構後：</h4>
 * <pre>
 * SalaryStructure structure = repository.findByEmployeeId(targetEmpId)
 *         .filter(SalaryStructure::isActive)
 *         .orElseThrow(...);
 * </pre>
 */
@Service("getEmployeeSalaryStructureServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEmployeeSalaryStructureServiceImpl
        implements QueryApiService<String, SalaryStructureResponse> {

    private final ISalaryStructureRepository repository;

    @Override
    public SalaryStructureResponse getResponse(
            String request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 從 args 取得目標員工 ID，若無則使用 request
        String targetEmpId = (args != null && args.length > 0) ? args[0] : request;

        if (targetEmpId == null || targetEmpId.isBlank()) {
            throw new IllegalArgumentException("員工 ID 為必填");
        }

        // 使用 Repository 提供的方法查詢最新生效的薪資結構，並過濾有效狀態
        SalaryStructure structure = repository.findByEmployeeId(targetEmpId)
                .filter(SalaryStructure::isActive)
                .orElseThrow(() -> new DomainException(
                        "SALARY_STRUCTURE_NOT_FOUND",
                        "找不到員工的有效薪資結構: " + targetEmpId));

        return SalaryStructureDtoFactory.toResponse(structure);
    }
}
