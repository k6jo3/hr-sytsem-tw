package com.company.hrms.payroll.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.Condition;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.valueobject.StructureId;

/**
 * 薪資結構 Repository 介面
 */
public interface ISalaryStructureRepository {

    /**
     * 儲存薪資結構
     *
     * @param structure 薪資結構
     * @return 儲存後的薪資結構
     */
    SalaryStructure save(SalaryStructure structure);

    /**
     * 根據 ID 查詢
     *
     * @param id 結構 ID
     * @return 薪資結構
     */
    Optional<SalaryStructure> findById(StructureId id);

    /**
     * 根據員工 ID 查詢 (最新生效)
     *
     * @param employeeId 員工 ID
     * @return 薪資結構
     */
    Optional<SalaryStructure> findByEmployeeId(String employeeId);

    /**
     * 分頁查詢 (使用 Condition 註解式條件)
     *
     * <p>推薦使用此方法，條件透過 DTO 上的 {@code @EQ}、{@code @LIKE} 等註解宣告，
     * 無需手動撰寫 if-else 判斷。</p>
     *
     * @param condition 條件包裝器 (包含查詢條件與分頁參數)
     * @param <C>       條件 DTO 類型
     * @return 分頁結果
     */
    <C> Page<SalaryStructure> findPageByCondition(Condition<C> condition);

    /**
     * 分頁查詢 (使用 QueryGroup)
     *
     * @param group    查詢條件群組
     * @param pageable 分頁參數
     * @return 分頁結果
     * @deprecated 建議使用 {@link #findPage(Condition)} 方法
     */
    @Deprecated
    Page<SalaryStructure> findAll(QueryGroup group, Pageable pageable);

    /**
     * 根據員工 ID 與日期查詢 (查詢該日期生效的結構)
     *
     * @param employeeId    員工 ID
     * @param effectiveDate 日期
     * @return 薪資結構
     */
    Optional<SalaryStructure> findByEmployeeAndEffectiveDate(String employeeId, LocalDate effectiveDate);

    /**
     * 查詢所有有效的薪資結構 (依薪資制度篩選)
     *
     * @param payrollSystem 薪資制度 (MONTHLY, HOURLY)
     * @return 有效的薪資結構列表
     */
    List<SalaryStructure> findAllActiveByPayrollSystem(String payrollSystem);
}
