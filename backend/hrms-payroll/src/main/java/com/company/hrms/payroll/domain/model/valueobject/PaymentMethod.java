package com.company.hrms.payroll.domain.model.valueobject;

/**
 * 領薪方式列舉
 * 定義薪資發放的方式
 */
public enum PaymentMethod {

    /**
     * 銀行匯款
     * 需確認員工已設定銀行帳號
     */
    BANK_TRANSFER,

    /**
     * 現金領取
     */
    CASH
}
