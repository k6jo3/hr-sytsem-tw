package com.company.hrms.iam.application.service.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

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
@Transactional
public class LogoutServiceImpl implements CommandApiService<Void, Void> {
    // TODO: 實作登出邏輯
    @Override
    public Void execCommand(Void request, JWTModel currentUser, String... args)
            throws Exception {
        // Stateless JWT: No server-side state to clear. Client should discard the
        // token.
        if (currentUser != null) {
            // Log logout event if/when logging service is available
        }
        return null;
    }
}
