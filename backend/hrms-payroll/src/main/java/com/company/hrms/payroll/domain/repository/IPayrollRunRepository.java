package com.company.hrms.payroll.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.common.query.QueryGroup;

/**
 * 薪資批次 Repository 介面
 */
public interface IPayrollRunRepository {

    /**
     * 儲存薪資批次
     * 
     * @param payrollRun 薪資批次
     * @return 儲存後的薪資批次
     */
    PayrollRun save(PayrollRun payrollRun);

    /**
     * 根據 ID 查詢
     * 
     * @param id 批次 ID
     * @return 薪資批次
     */
    Optional<PayrollRun> findById(RunId id);

    /**
     * 根據組織查詢
     * 
     * @param organizationId 組織 ID
     * @return 薪資批次列表
     */
    List<PayrollRun> findByOrganization(String organizationId);

    /**
     * 根據組織與計薪期間查詢 (檢查是否已存在)
     * 
     * @param organizationId 組織 ID
     * @param payPeriod      計薪期間 (通常比對年月)
     * @return 薪資批次
     */
    Optional<PayrollRun> findByOrganizationAndPeriod(String organizationId, PayPeriod payPeriod);

    /**
     * 查詢所有 (支援分頁與過濾)
     * 
     * @param group    查詢條件
     * @param pageable 分頁資訊
     * @return 分頁結果
     */
    org.springframework.data.domain.Page<PayrollRun> findAll(QueryGroup group,
            org.springframework.data.domain.Pageable pageable);
}
