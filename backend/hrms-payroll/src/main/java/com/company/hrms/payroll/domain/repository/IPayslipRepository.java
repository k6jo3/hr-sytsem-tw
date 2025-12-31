package com.company.hrms.payroll.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.valueobject.PayslipId;
import com.company.hrms.payroll.domain.model.valueobject.RunId;

/**
 * 薪資單 Repository 介面
 */
public interface IPayslipRepository {

    /**
     * 儲存薪資單
     * 
     * @param payslip 薪資單
     * @return 儲存後的薪資單
     */
    Payslip save(Payslip payslip);

    /**
     * 批次儲存薪資單
     * 
     * @param payslips 薪資單列表
     */
    void saveAllPayslips(List<Payslip> payslips);

    /**
     * 根據 ID 查詢
     * 
     * @param id 薪資單 ID
     * @return 薪資單
     */
    Optional<Payslip> findById(PayslipId id);

    /**
     * 根據批次 ID 查詢
     * 
     * @param runId 批次 ID
     * @return 薪資單列表
     */
    List<Payslip> findByPayrollRun(RunId runId);

    /**
     * 根據員工 ID 查詢
     * 
     * @param employeeId 員工 ID
     * @return 薪資單列表
     */
    List<Payslip> findByEmployeeId(String employeeId);

    /**
     * 根據員工 ID 與年度查詢 (ESS 用)
     * 
     * @param employeeId 員工 ID
     * @param year       年度
     * @return 薪資單列表
     */
    List<Payslip> findByEmployeeAndYear(String employeeId, int year);

    /**
     * 查詢所有 (支援分頁與過濾)
     * 
     * @param group    查詢條件
     * @param pageable 分頁資訊
     * @return 分頁結果
     */
    org.springframework.data.domain.Page<Payslip> findAll(com.company.hrms.common.query.QueryGroup group,
            org.springframework.data.domain.Pageable pageable);
}
