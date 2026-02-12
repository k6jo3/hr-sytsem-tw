package com.company.hrms.iam.application.service.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.user.BatchDeactivateUsersRequest;
import com.company.hrms.iam.api.response.user.BatchDeactivateUsersResponse;
import com.company.hrms.iam.domain.event.UsersBatchDeactivatedEvent;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;
import com.company.hrms.iam.domain.repository.IUserRepository;

/**
 * 批次停用使用者 Application Service
 * 
 * <p>
 * 對應 API: PUT /api/v1/users/batch-deactivate
 * </p>
 */
@Service("batchDeactivateUsersServiceImpl")
@Transactional
public class BatchDeactivateUsersServiceImpl
        implements CommandApiService<BatchDeactivateUsersRequest, BatchDeactivateUsersResponse> {

    private final IUserRepository userRepository;
    private final EventPublisher eventPublisher;

    public BatchDeactivateUsersServiceImpl(IUserRepository userRepository, EventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public BatchDeactivateUsersResponse execCommand(BatchDeactivateUsersRequest request,
            JWTModel currentUser,
            String... args) throws Exception {
        List<String> successIds = new ArrayList<>();
        List<BatchDeactivateUsersResponse.FailedUser> failedUsers = new ArrayList<>();

        for (String userId : request.getUserIds()) {
            try {
                // 載入使用者
                User user = userRepository.findById(new UserId(userId))
                        .orElse(null);

                if (user == null) {
                    failedUsers.add(BatchDeactivateUsersResponse.FailedUser.builder()
                            .userId(userId)
                            .reason("使用者不存在")
                            .build());
                    continue;
                }

                // 檢查是否已停用
                if (user.getStatus() == UserStatus.INACTIVE) {
                    failedUsers.add(BatchDeactivateUsersResponse.FailedUser.builder()
                            .userId(userId)
                            .reason("使用者已處於停用狀態")
                            .build());
                    continue;
                }

                // 停用使用者
                user.deactivate();
                userRepository.update(user);
                successIds.add(userId);

            } catch (Exception e) {
                failedUsers.add(BatchDeactivateUsersResponse.FailedUser.builder()
                        .userId(userId)
                        .reason(e.getMessage())
                        .build());
            }
        }

        // 發布批次停用事件
        if (!successIds.isEmpty()) {
            eventPublisher.publish(new UsersBatchDeactivatedEvent(successIds, successIds.size()));
        }

        return BatchDeactivateUsersResponse.builder()
                .successIds(successIds)
                .failedUsers(failedUsers)
                .successCount(successIds.size())
                .failedCount(failedUsers.size())
                .build();
    }
}
