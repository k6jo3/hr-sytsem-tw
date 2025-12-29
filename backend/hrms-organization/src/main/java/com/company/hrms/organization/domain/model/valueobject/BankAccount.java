package com.company.hrms.organization.domain.model.valueobject;

import com.company.hrms.common.exception.DomainException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class BankAccount {
    private final String bankCode;
    private final String bankName;
    private final String branchCode;
    private final String accountNumber;
    private final String accountHolderName;

    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "***";
        }
        return "***" + accountNumber.substring(accountNumber.length() - 4);
    }

    public String getBankDescription() {
        return bankName != null ? bankName : bankCode;
    }

    public void validate() {}

    @Override
    public String toString() {
        return getBankDescription() + " " + getMaskedAccountNumber();
    }
}
