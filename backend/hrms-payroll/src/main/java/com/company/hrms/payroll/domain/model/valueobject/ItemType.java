package com.company.hrms.payroll.domain.model.valueobject;

/**
 * 薪資項目類型列舉
 * 區分收入項與扣除項
 */
public enum ItemType {

    /**
     * 收入項
     * 加到薪資總額的項目 (如: 底薪、津貼、獎金)
     */
    EARNING,

    /**
     * 扣除項
     * 從薪資總額扣除的項目 (如: 勞保、健保、所得稅)
     */
    DEDUCTION
}
