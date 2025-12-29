package com.company.hrms.iam.application.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.user.UpdateUserRequest;
import com.company.hrms.iam.api.response.user.UserDetailResponse;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.application.service.user.task.*;

/**
 * 更新使用者 Application Service
 */
@Service("updateUserServiceImpl")
@Transactional
public class UpdateUserServiceImpl implements CommandApiService<UpdateUserRequest, UserDetailResponse> {

    private final LoadUserTask loadUserTask;
    private final UpdateProfileTask updateProfileTask;
    private final SaveUserTask saveUserTask;
    private final PublishUserUpdatedEventTask publishUserUpdatedEventTask;

    public UpdateUserServiceImpl(LoadUserTask loadUserTask,
                                 UpdateProfileTask updateProfileTask,
                                 SaveUserTask saveUserTask,
                                 PublishUserUpdatedEventTask publishUserUpdatedEventTask) {
        this.loadUserTask = loadUserTask;
        this.updateProfileTask = updateProfileTask;
        this.saveUserTask = saveUserTask;
        this.publishUserUpdatedEventTask = publishUserUpdatedEventTask;
    }

    @Override
    public UserDetailResponse execCommand(UpdateUserRequest request, JWTModel currentUser, String... args) throws Exception {
        String userId = args[0];

        UserPipelineContext context = new UserPipelineContext(userId, request);

        BusinessPipeline.start(context)
            .next(loadUserTask)
            .next(updateProfileTask)
            .next(saveUserTask)
            .next(publishUserUpdatedEventTask)
            .execute();

        var user = context.getUser();
        return UserDetailResponse.builder()
                .userId(user.getId().getValue())
                .username(user.getUsername())
                .email(user.getEmail().getValue())
                .displayName(user.getDisplayName())
                .status(user.getStatus().name())
                .build();
    }
}
