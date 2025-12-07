package com.company.hrms.iam.domain.service;

import com.company.hrms.iam.domain.model.aggregate.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 帳號鎖定 Domain Service
 * 負責處理登入失敗後的帳號鎖定邏輯
 */
@Service
public class AccountLockingDomainService {

    /**
     * 最大登入失敗次數
     */
    private static final int MAX_FAILED_ATTEMPTS = 5;

    /**
     * 鎖定時間 (分鐘)
     */
    private static final int LOCK_DURATION_MINUTES = 30;

    /**
     * 記錄登入失敗並檢查是否需要鎖定
     * @param user 使用者
     * @return 是否已被鎖定
     */
    public boolean recordFailureAndCheckLock(User user) {
        user.incrementFailedAttempts();

        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.lock(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            return true;
        }

        return false;
    }

    /**
     * 檢查帳號是否已解鎖 (鎖定時間已過期)
     * @param user 使用者
     * @return 是否已自動解鎖
     */
    public boolean checkAndUnlock(User user) {
        if (user.isLocked() && user.getLockedUntil() != null 
                && user.getLockedUntil().isBefore(LocalDateTime.now())) {
            user.unlock();
            return true;
        }
        return false;
    }

    /**
     * 取得最大登入失敗次數
     * @return 最大登入失敗次數
     */
    public int getMaxFailedAttempts() {
        return MAX_FAILED_ATTEMPTS;
    }

    /**
     * 取得鎖定時間 (分鐘)
     * @return 鎖定時間
     */
    public int getLockDurationMinutes() {
        return LOCK_DURATION_MINUTES;
    }
}
