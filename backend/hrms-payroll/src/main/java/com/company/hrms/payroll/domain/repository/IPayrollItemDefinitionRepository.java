package com.company.hrms.payroll.domain.repository;

import java.util.List;

import com.company.hrms.payroll.domain.model.valueobject.TaxBracket;

/**
 * 薪資項目定義與參數 Repository 介面
 * 負責存取薪資項目定義、稅率級距等參數
 */
public interface IPayrollItemDefinitionRepository {

    // 暫無項目定義實體，先定義參數獲取方法

    /**
     * 取得所得稅級距表
     * 
     * @param year 年度
     * @return 級距列表
     */
    List<TaxBracket> getTaxBrackets(int year);
}
