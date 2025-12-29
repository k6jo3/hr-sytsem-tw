package com.company.hrms.iam.application.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.user.CreateUserRequest;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.iam.api.response.user.CreateUserResponse;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.application.service.user.task.*;

/**
 * 新增使用者 Application Service
 * 
 * <p>
 * 命名規範：{動詞}{名詞}ServiceImpl
 * </p>
 * <p>
 * 對應 Controller 方法：createUser
 * </p>
 */
@Service("createUserServiceImpl")
@Transactional
public class CreateUserServiceImpl
        implements CommandApiService<CreateUserRequest, CreateUserResponse> {

    private final CheckUserExistenceTask checkUserExistenceTask;
    private final HashPasswordTask hashPasswordTask;
    private final CreateUserAggregateTask createUserAggregateTask;
    private final SaveUserTask saveUserTask;
    private final PublishUserEventTask publishUserEventTask;

    public CreateUserServiceImpl(CheckUserExistenceTask checkUserExistenceTask,
                                 HashPasswordTask hashPasswordTask,
                                 CreateUserAggregateTask createUserAggregateTask,
                                 SaveUserTask saveUserTask,
                                 PublishUserEventTask publishUserEventTask) {
        this.checkUserExistenceTask = checkUserExistenceTask;
        this.hashPasswordTask = hashPasswordTask;
        this.createUserAggregateTask = createUserAggregateTask;
        this.saveUserTask = saveUserTask;
        this.publishUserEventTask = publishUserEventTask;
    }

    /**
     * 執行新增使用者 (Pipeline 模式)
     */
    @Override
    public CreateUserResponse execCommand(CreateUserRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        UserPipelineContext context = new UserPipelineContext(request);

        BusinessPipeline.start(context)
                .next(checkUserExistenceTask)
                .next(hashPasswordTask)
                .next(createUserAggregateTask)
                .next(saveUserTask)
                .next(publishUserEventTask)
                .execute();

        return CreateUserResponse.builder()
                .userId(context.getUser().getId().getValue())
                .username(context.getUser().getUsername())
                .message("使用者建立成功")
                .build();
    }
}
