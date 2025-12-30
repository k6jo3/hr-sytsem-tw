package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.AccountLockingDomainService;

import lombok.RequiredArgsConstructor;

/**
 * 檢查使用者狀態 Task
 */
@Component
@RequiredArgsConstructor
public class CheckUserStatusTask implements PipelineTask<AuthContext> {

    private final IUserRepository userRepository;
    private final AccountLockingDomainService accountLockingService;

    @Override
    public void execute(AuthContext context) throws Exception {
        var user = context.getUser();

        // 檢查是否已停用
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DomainException("USER_INACTIVE", "使用者帳號已停用");
        }

        // 檢查是否已刪除
        if (user.getStatus() == UserStatus.DELETED) {
            throw new DomainException("USER_DELETED", "使用者帳號已刪除");
        }

        // 檢查是否被鎖定
        if (user.isLocked()) {
            // 嘗試自動解鎖 (如果鎖定時間已過)
            if (!accountLockingService.checkAndUnlock(user)) {
                throw new DomainException("USER_LOCKED",
                        String.format("帳號已鎖定，請於 %s 後再試", user.getLockedUntil()));
            }
            userRepository.update(user);
        }
    }

    @Override
    public String getName() {
        return "檢查使用者狀態";
    }
}
