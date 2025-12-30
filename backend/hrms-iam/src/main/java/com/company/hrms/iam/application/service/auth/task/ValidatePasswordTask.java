package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.AccountLockingDomainService;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

import lombok.RequiredArgsConstructor;

/**
 * 驗證密碼 Task
 */
@Component
@RequiredArgsConstructor
public class ValidatePasswordTask implements PipelineTask<AuthContext> {

    private final PasswordHashingDomainService passwordHashingService;
    private final AccountLockingDomainService accountLockingService;
    private final IUserRepository userRepository;

    @Override
    public void execute(AuthContext context) throws Exception {
        var user = context.getUser();
        var request = context.getLoginRequest();

        if (!passwordHashingService.verify(request.getPassword(), user.getPasswordHash())) {
            // 記錄失敗並檢查是否鎖定
            boolean locked = accountLockingService.recordFailureAndCheckLock(user);
            userRepository.update(user);

            if (locked) {
                throw new DomainException("USER_LOCKED",
                        String.format("登入失敗次數過多，帳號已鎖定 %d 分鐘",
                                accountLockingService.getLockDurationMinutes()));
            }

            int remainingAttempts = accountLockingService.getMaxFailedAttempts()
                    - user.getFailedLoginAttempts();
            throw new DomainException("LOGIN_FAILED",
                    String.format("使用者名稱或密碼錯誤，剩餘嘗試次數: %d", remainingAttempts));
        }
    }

    @Override
    public String getName() {
        return "驗證密碼";
    }
}
