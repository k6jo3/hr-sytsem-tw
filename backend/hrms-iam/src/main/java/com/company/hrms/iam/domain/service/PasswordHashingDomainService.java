package com.company.hrms.iam.domain.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 密碼雜湊 Domain Service
 * 負責密碼的雜湊與驗證
 *
 * <p>使用 BCrypt 演算法，work factor = 12</p>
 */
@Service
public class PasswordHashingDomainService {

    /**
     * BCrypt work factor
     * 數值越高，計算時間越長，安全性越高
     */
    private static final int BCRYPT_STRENGTH = 12;

    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 建構子
     */
    public PasswordHashingDomainService() {
        this.passwordEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    /**
     * 雜湊密碼
     * @param rawPassword 原始密碼
     * @return 雜湊後的密碼
     */
    public String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("密碼不可為空");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 驗證密碼
     * @param rawPassword 原始密碼
     * @param hashedPassword 雜湊後的密碼
     * @return 是否匹配
     */
    public boolean verify(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
