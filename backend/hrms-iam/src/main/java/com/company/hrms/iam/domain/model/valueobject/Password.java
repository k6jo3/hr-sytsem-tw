package com.company.hrms.iam.domain.model.valueobject;

import com.company.hrms.common.exception.DomainException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Password 值物件
 * 封裝密碼強度驗證邏輯
 *
 * <p>密碼要求：</p>
 * <ul>
 *   <li>至少 8 個字元</li>
 *   <li>至少一個大寫字母</li>
 *   <li>至少一個小寫字母</li>
 *   <li>至少一個數字</li>
 *   <li>至少一個特殊字元 (!@#$%^&*()_+-=[]{}|;':\",./<>?)</li>
 * </ul>
 */
@Getter
@EqualsAndHashCode
public class Password {

    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?]");

    private final String value;

    /**
     * 私有建構子，使用工廠方法建立
     */
    private Password(String value) {
        this.value = value;
    }

    /**
     * 工廠方法：建立並驗證密碼
     * @param rawPassword 原始密碼
     * @return Password 值物件
     * @throws DomainException 若密碼不符合強度要求
     */
    public static Password of(String rawPassword) {
        validate(rawPassword);
        return new Password(rawPassword);
    }

    /**
     * 驗證密碼強度
     * @param password 密碼
     * @throws DomainException 若密碼不符合要求
     */
    private static void validate(String password) {
        // 檢查是否為空
        if (password == null || password.isBlank()) {
            throw new DomainException("PASSWORD_REQUIRED", "密碼不可為空");
        }

        // 檢查長度
        if (password.length() < MIN_LENGTH) {
            throw new DomainException("PASSWORD_TOO_SHORT",
                    String.format("密碼長度必須至少 %d 個字元", MIN_LENGTH));
        }

        // 檢查密碼強度
        List<String> missingRequirements = new ArrayList<>();

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            missingRequirements.add("大寫字母");
        }
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            missingRequirements.add("小寫字母");
        }
        if (!DIGIT_PATTERN.matcher(password).find()) {
            missingRequirements.add("數字");
        }
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            missingRequirements.add("特殊字元");
        }

        if (!missingRequirements.isEmpty()) {
            throw new DomainException("PASSWORD_WEAK",
                    "密碼強度不足，需包含: " + String.join(", ", missingRequirements));
        }
    }

    /**
     * 檢查密碼是否符合強度要求
     * @return 是否為強密碼
     */
    public boolean isStrong() {
        return UPPERCASE_PATTERN.matcher(value).find()
                && LOWERCASE_PATTERN.matcher(value).find()
                && DIGIT_PATTERN.matcher(value).find()
                && SPECIAL_CHAR_PATTERN.matcher(value).find()
                && value.length() >= MIN_LENGTH;
    }

    @Override
    public String toString() {
        // 不要洩露密碼內容
        return "********";
    }
}
