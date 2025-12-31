package com.company.hrms.payroll.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 銀行帳戶值物件
 * 封裝薪轉銀行帳戶資訊，提供帳號遮罩功能
 */
@Getter
@EqualsAndHashCode
public class BankAccount {

    /**
     * 銀行代碼 (3 碼)
     */
    private final String bankCode;

    /**
     * 銀行名稱
     */
    private final String bankName;

    /**
     * 分行代碼 (4 碼)
     */
    private final String branchCode;

    /**
     * 帳號 (保存完整帳號，顯示時遮罩)
     */
    private final String accountNumber;

    /**
     * 帳戶持有人姓名
     */
    private final String accountHolder;

    /**
     * 建構銀行帳戶值物件
     * 
     * @param bankCode      銀行代碼
     * @param bankName      銀行名稱
     * @param branchCode    分行代碼
     * @param accountNumber 帳號
     * @param accountHolder 帳戶持有人姓名
     */
    public BankAccount(String bankCode, String bankName, String branchCode,
            String accountNumber, String accountHolder) {
        if (bankCode == null || bankCode.isBlank()) {
            throw new IllegalArgumentException("Bank code cannot be null or blank");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be null or blank");
        }

        this.bankCode = bankCode;
        this.bankName = bankName;
        this.branchCode = branchCode;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
    }

    /**
     * 建立銀行帳戶 (簡化版，不含分行)
     * 
     * @param bankCode      銀行代碼
     * @param bankName      銀行名稱
     * @param accountNumber 帳號
     * @param accountHolder 帳戶持有人姓名
     * @return 銀行帳戶
     */
    public static BankAccount of(String bankCode, String bankName,
            String accountNumber, String accountHolder) {
        return new BankAccount(bankCode, bankName, null, accountNumber, accountHolder);
    }

    /**
     * 取得遮罩後的帳號
     * 只顯示前 4 碼與後 4 碼，中間以 * 替代
     * 
     * @return 遮罩後的帳號
     */
    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() <= 8) {
            return "****";
        }
        String prefix = accountNumber.substring(0, 4);
        String suffix = accountNumber.substring(accountNumber.length() - 4);
        int maskLength = accountNumber.length() - 8;
        String mask = "*".repeat(maskLength);
        return prefix + mask + suffix;
    }

    /**
     * 取得銀行薪轉檔格式的帳號 (無遮罩)
     * 僅供產生薪轉媒體檔使用
     * 
     * @return 完整帳號
     */
    public String getFullAccountNumber() {
        return accountNumber;
    }

    /**
     * 取得銀行代碼與名稱的組合
     * 
     * @return 例如: "004 台灣銀行"
     */
    public String getBankDisplay() {
        return bankCode + " " + (bankName != null ? bankName : "");
    }

    @Override
    public String toString() {
        return getBankDisplay() + " " + getMaskedAccountNumber();
    }
}
