package com.company.hrms.iam.application.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.application.service.user.task.*;

/**
 * 刪除使用者 Application Service
 */
@Service("deleteUserServiceImpl")
@Transactional
public class DeleteUserServiceImpl implements CommandApiService<Void, Void> {

    private final LoadUserTask loadUserTask;
    private final DeleteUserTask deleteUserTask;
    private final PublishUserDeletedEventTask publishUserDeletedEventTask;

    public DeleteUserServiceImpl(LoadUserTask loadUserTask,
                                 DeleteUserTask deleteUserTask,
                                 PublishUserDeletedEventTask publishUserDeletedEventTask) {
        this.loadUserTask = loadUserTask;
        this.deleteUserTask = deleteUserTask;
        this.publishUserDeletedEventTask = publishUserDeletedEventTask;
    }

    @Override
    public Void execCommand(Void request, JWTModel currentUser, String... args) throws Exception {
        String userId = args[0];

        UserPipelineContext context = new UserPipelineContext(userId);

        BusinessPipeline.start(context)
            .next(loadUserTask)
            .next(deleteUserTask)
            .next(publishUserDeletedEventTask)
            .execute();

        return null;
    }
}
