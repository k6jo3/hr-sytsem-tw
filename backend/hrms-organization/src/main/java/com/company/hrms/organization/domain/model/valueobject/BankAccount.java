package com.company.hrms.organization.domain.model.valueobject;

import com.company.hrms.common.exception.DomainException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 銀行帳戶值對象
 * 封裝銀行帳戶資訊
 */
@Getter
@Builder
@EqualsAndHashCode
public class BankAccount {

    /**
     * 銀行代碼
     */
    private final String bankCode;

    /**
     * 銀行名稱
     */
    private final String bankName;

    /**
     * 分行代碼
     */
    private final String branchCode;

    /**
     * 帳號 (敏感資訊，應加密儲存)
     */
    private final String accountNumber;

    /**
     * 戶名
     */
    private final String accountHolderName;

    /**
     * 取得遮罩後的帳號 (顯示用)
     * @return 遮罩後的帳號，如 ***6789
     */
    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "***";
        }
        return "***" + accountNumber.substring(accountNumber.length() - 4);
    }

    /**
     * 取得完整銀行資訊描述
     * @return 銀行資訊描述
     */
    public String getBankDescription() {
        StringBuilder sb = new StringBuilder();
        if (bankName != null && !bankName.isBlank()) {
            sb.append(bankName);
        } else if (bankCode != null && !bankCode.isBlank()) {
            sb.append("銀行代碼: ").append(bankCode);
        }
        return sb.toString();
    }

    /**
     * 檢查是否有有效的銀行帳戶資料
     * @return 是否有效
     */
    public boolean isValid() {
        return accountNumber != null && !accountNumber.isBlank();
    }

    /**
     * 驗證銀行帳戶資料完整性
     * @throws DomainException 若資料不完整
     */
    public void validate() {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new DomainException("BANK_ACCOUNT_NUMBER_REQUIRED", "銀行帳號不可為空");
        }
        if (bankCode == null || bankCode.isBlank()) {
            throw new DomainException("BANK_CODE_REQUIRED", "銀行代碼不可為空");
        }
    }

    @Override
    public String toString() {
        return getBankDescription() + " " + getMaskedAccountNumber();
    }

    /**
     * 建立空的銀行帳戶
     * @return 空實例
     */
    public static BankAccount empty() {
        return BankAccount.builder().build();
    }
}
