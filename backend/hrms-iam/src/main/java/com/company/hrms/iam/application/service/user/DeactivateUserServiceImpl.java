package com.company.hrms.iam.application.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.application.service.user.task.*;

/**
 * 停用使用者 Application Service
 */
@Service("deactivateUserServiceImpl")
@Transactional
public class DeactivateUserServiceImpl implements CommandApiService<Void, Void> {

    private final LoadUserTask loadUserTask;
    private final DeactivateUserTask deactivateUserTask;
    private final SaveUserTask saveUserTask;
    private final PublishUserDeactivatedEventTask publishUserDeactivatedEventTask;

    public DeactivateUserServiceImpl(LoadUserTask loadUserTask,
                                     DeactivateUserTask deactivateUserTask,
                                     SaveUserTask saveUserTask,
                                     PublishUserDeactivatedEventTask publishUserDeactivatedEventTask) {
        this.loadUserTask = loadUserTask;
        this.deactivateUserTask = deactivateUserTask;
        this.saveUserTask = saveUserTask;
        this.publishUserDeactivatedEventTask = publishUserDeactivatedEventTask;
    }

    @Override
    public Void execCommand(Void request, JWTModel currentUser, String... args) throws Exception {
        String userId = args[0];

        UserPipelineContext context = new UserPipelineContext(userId);

        BusinessPipeline.start(context)
            .next(loadUserTask)
            .next(deactivateUserTask)
            .next(saveUserTask)
            .next(publishUserDeactivatedEventTask)
            .execute();

        return null;
    }
}
