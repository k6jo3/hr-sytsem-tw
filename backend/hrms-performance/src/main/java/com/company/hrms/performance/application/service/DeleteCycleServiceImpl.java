package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.DeleteCycleRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.application.service.context.StartCycleContext;
import com.company.hrms.performance.application.service.task.LoadCycleTask;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 刪除考核週期 Service (Business Pipeline 架構)
 */
@Service("deleteCycleServiceImpl")
@RequiredArgsConstructor
@Transactional
public class DeleteCycleServiceImpl implements CommandApiService<DeleteCycleRequest, SuccessResponse> {

    private final LoadCycleTask loadCycleTask;
    private final IPerformanceCycleRepository cycleRepository;

    @Override
    public SuccessResponse execCommand(DeleteCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        StartCycleContext ctx = new StartCycleContext(req.getCycleId());

        // 2-step pipeline: Load → Delete
        BusinessPipeline.start(ctx)
                .next(loadCycleTask)
                .execute();

        // Delete after pipeline (not in pipeline to keep simplicity)
        cycleRepository.delete(ctx.getCycle());

        return SuccessResponse.of("考核週期已刪除");
    }
}
