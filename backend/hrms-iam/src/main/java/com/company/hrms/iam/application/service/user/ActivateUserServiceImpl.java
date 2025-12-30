package com.company.hrms.iam.application.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.application.service.user.task.ActivateUserTask;
import com.company.hrms.iam.application.service.user.task.LoadUserTask;
import com.company.hrms.iam.application.service.user.task.SaveUserTask;

/**
 * 啟用使用者 Application Service
 * 
 * <p>
 * 對應 API: PUT /api/v1/users/{userId}/activate
 * </p>
 */
@Service("activateUserServiceImpl")
@Transactional
public class ActivateUserServiceImpl implements CommandApiService<Void, Void> {

    private final LoadUserTask loadUserTask;
    private final ActivateUserTask activateUserTask;
    private final SaveUserTask saveUserTask;

    public ActivateUserServiceImpl(LoadUserTask loadUserTask,
            ActivateUserTask activateUserTask,
            SaveUserTask saveUserTask) {
        this.loadUserTask = loadUserTask;
        this.activateUserTask = activateUserTask;
        this.saveUserTask = saveUserTask;
    }

    @Override
    public Void execCommand(Void request, JWTModel currentUser, String... args) throws Exception {
        String userId = args[0];

        UserPipelineContext context = new UserPipelineContext(userId);

        BusinessPipeline.start(context)
                .next(loadUserTask)
                .next(activateUserTask)
                .next(saveUserTask)
                .execute();

        return null;
    }
}
