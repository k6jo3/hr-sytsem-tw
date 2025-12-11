package com.company.hrms.iam.domain.model.valueobject;

import com.company.hrms.common.exception.DomainException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

/**
 * Email 值物件
 * 封裝 Email 驗證邏輯
 */
@Getter
@EqualsAndHashCode
public class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private final String value;

    /**
     * 建構 Email 值物件
     * @param value Email 地址
     * @throws DomainException 若 Email 格式無效
     */
    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("EMAIL_REQUIRED", "Email 不可為空");
        }
        // 先去除前後空白並轉為小寫，再進行驗證
        String normalized = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new DomainException("EMAIL_INVALID", "Email 格式無效: " + normalized);
        }
        this.value = normalized;
    }

    @Override
    public String toString() {
        return value;
    }
}
