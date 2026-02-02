package com.company.hrms.iam.application.service.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.domain.event.UserLoggedOutEvent;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 登出 Application Service
 *
 * <p>
 * 命名規範：{動詞}{名詞}ServiceImpl
 * </p>
 * <p>
 * 對應 Controller 方法：logout
 * </p>
 */
@Service("logoutServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LogoutServiceImpl implements CommandApiService<Void, Void> {

    private final IUserRepository userRepository;
    private final EventPublisher eventPublisher;

    @Override
    public Void execCommand(Void request, JWTModel currentUser, String... args)
            throws Exception {

        if (currentUser == null || currentUser.getUserId() == null) {
            log.warn("無效的登出請求: 沒有當前使用者資訊");
            return null;
        }

        String userId = currentUser.getUserId();
        String username = currentUser.getUsername();

        log.info("使用者登出: userId={}, username={}", userId, username);

        // 1. 記錄登出時間 (選用實體化記錄)
        userRepository.findById(new UserId(userId)).ifPresent(user -> {
            user.recordLogout();
            userRepository.update(user);
        });

        // 2. 發布登出領域事件 (可供其他微服務或審計日誌使用)
        eventPublisher.publish(new UserLoggedOutEvent(userId, username));

        // TODO: 後端沒有token的驗證機制判斷失效的token或使token失效?
        // 註：對於無狀態 Token (JWT)，伺服器端不需要（也無法單方面）使 Token 失效。
        // 前端應負責清除存儲的 Token。

        return null;
    }
}
