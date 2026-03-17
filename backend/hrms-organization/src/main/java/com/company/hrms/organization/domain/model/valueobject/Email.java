package com.company.hrms.organization.domain.model.valueobject;

import com.company.hrms.common.exception.DomainException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

/**
 * Email 值對象
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
     * 從持久層重建 Email 值對象（跳過驗證）
     * 用於從 DB 讀取已存儲的資料時，避免查詢路徑觸發 Domain 驗證
     *
     * @param value 資料庫中的 Email 地址
     * @return Email 實例，若 value 為 null 或空白則回傳 null
     */
    public static Email reconstitute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new Email(value, false);
    }

    /**
     * 內部建構子（可選擇是否驗證）
     *
     * @param value    Email 地址
     * @param validate 是否進行格式驗證
     */
    private Email(String value, boolean validate) {
        if (value == null || value.isBlank()) {
            throw new DomainException("EMAIL_REQUIRED", "Email 不可為空");
        }
        String normalized = value.trim().toLowerCase();
        if (validate && !EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new DomainException("EMAIL_INVALID", "Email 格式無效: " + normalized);
        }
        this.value = normalized;
    }

    /**
     * 建構 Email 值對象（含完整驗證）
     * 用於新增/更新時，確保 Email 格式正確
     *
     * @param value Email 地址
     * @throws DomainException 若 Email 格式無效
     */
    public Email(String value) {
        this(value, true);
    }

    /**
     * 取得 Email 的域名部分
     * @return 域名
     */
    public String getDomain() {
        int atIndex = value.indexOf('@');
        return value.substring(atIndex + 1);
    }

    /**
     * 取得 Email 的使用者名稱部分
     * @return 使用者名稱
     */
    public String getUsername() {
        int atIndex = value.indexOf('@');
        return value.substring(0, atIndex);
    }

    @Override
    public String toString() {
        return value;
    }
}
