package com.company.hrms.organization.domain.model.valueobject;

import java.util.regex.Pattern;

import com.company.hrms.common.exception.DomainException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 身分證號值對象
 * 封裝台灣身分證字號驗證邏輯
 */
@Getter
@EqualsAndHashCode
public class NationalId {

    /**
     * 台灣身分證字號格式: 一個大寫英文字母 + 9個數字
     */
    private static final Pattern NATIONAL_ID_PATTERN = Pattern.compile("^[A-Z][12]\\d{8}$");

    /**
     * 字母對應數字表 (用於驗證碼計算)
     */
    private static final int[] LETTER_MAP = {
            10, 11, 12, 13, 14, 15, 16, 17, 34, 18, 19, 20, 21, // A-M
            22, 35, 23, 24, 25, 26, 27, 28, 29, 32, 30, 31, 33 // N-Z
    };

    private final String value;

    /**
     * 從持久層重建身分證號值對象（跳過驗證）
     * 用於從 DB 讀取已存儲的資料時，避免查詢路徑觸發 Domain 驗證
     *
     * @param value 資料庫中的身分證字號
     * @return NationalId 實例，若 value 為 null 或空白則回傳 null
     */
    public static NationalId reconstitute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new NationalId(value, false);
    }

    /**
     * 內部建構子（可選擇是否驗證）
     *
     * @param value    身分證字號
     * @param validate 是否進行格式與 checksum 驗證
     */
    private NationalId(String value, boolean validate) {
        if (value == null || value.isBlank()) {
            throw new DomainException("NATIONAL_ID_REQUIRED", "身分證字號不可為空");
        }
        String normalized = value.trim().toUpperCase();
        if (validate) {
            if (!NATIONAL_ID_PATTERN.matcher(normalized).matches()) {
                throw new DomainException("NATIONAL_ID_FORMAT_INVALID", "身分證字號格式無效");
            }
            if (!validateChecksum(normalized)) {
                throw new DomainException("NATIONAL_ID_CHECKSUM_INVALID", "身分證字號檢核錯誤");
            }
        }
        this.value = normalized;
    }

    /**
     * 建構身分證號值對象（含完整驗證）
     * 用於新增/更新員工時，確保身分證字號格式與 checksum 正確
     *
     * @param value 身分證字號
     * @throws DomainException 若格式無效或驗證碼錯誤
     */
    public NationalId(String value) {
        this(value, true);
    }

    /**
     * 驗證身分證字號驗證碼
     * 
     * @param id 身分證字號
     * @return 驗證碼是否正確
     */
    private static boolean validateChecksum(String id) {
        // 將字母轉換為數字
        int letterValue = LETTER_MAP[id.charAt(0) - 'A'];
        int n1 = letterValue / 10;
        int n2 = letterValue % 10;

        // 計算驗證碼
        int sum = n1 * 1 + n2 * 9;

        int[] weights = { 8, 7, 6, 5, 4, 3, 2, 1, 1 };
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(id.charAt(i + 1)) * weights[i];
        }

        return sum % 10 == 0;
    }

    /**
     * 取得遮罩後的身分證字號 (顯示用)
     * 
     * @return 遮罩後的字號，如 A12***6789
     */
    public String getMaskedValue() {
        if (value.length() != 10) {
            return "***";
        }
        return value.substring(0, 3) + "***" + value.substring(6);
    }

    @Override
    public String toString() {
        return getMaskedValue();
    }
}
