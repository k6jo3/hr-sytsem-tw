package com.company.hrms.iam.application.service.auth;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 登出 Application Service
 *
 * <p>命名規範：{動詞}{名詞}ServiceImpl</p>
 * <p>對應 Controller 方法：logout</p>
 */
@Service("logoutServiceImpl")
@Transactional
public class LogoutServiceImpl implements CommandApiService<Void, Void> {

    // TODO: 注入 IRefreshTokenRepository 來撤銷 Token

    @Override
    public Void execCommand(Void request, JWTModel currentUser, String... args) throws Exception {
        if (currentUser != null) {
            // TODO: 1. 撤銷該用戶的 Refresh Token
            // refreshTokenRepository.revokeByUserId(currentUser.getUserId());

            // TODO: 2. 可選：將當前 Access Token 加入黑名單 (使用 Redis)
            // tokenBlacklistService.addToBlacklist(currentUser.getToken());

            // TODO: 3. 記錄登出日誌
            // loginLogService.logLogout(currentUser);
        }

        return null;
    }
}
