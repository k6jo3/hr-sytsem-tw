package com.company.hrms.attendance.application.service.overtime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.overtime.RejectOvertimeRequest;
import com.company.hrms.attendance.api.response.overtime.RejectOvertimeResponse;
import com.company.hrms.attendance.application.service.overtime.context.RejectOvertimeContext;
import com.company.hrms.attendance.application.service.overtime.task.LoadOvertimeForRejectionTask;
import com.company.hrms.attendance.application.service.overtime.task.PerformRejectOvertimeTask;
import com.company.hrms.attendance.application.service.overtime.task.SaveOvertimeForRejectionTask;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 加班駁回服務實作
 */
@Service("rejectOvertimeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RejectOvertimeServiceImpl implements CommandApiService<RejectOvertimeRequest, RejectOvertimeResponse> {

    private final LoadOvertimeForRejectionTask loadOvertimeForRejectionTask;
    private final PerformRejectOvertimeTask performRejectOvertimeTask;
    private final SaveOvertimeForRejectionTask saveOvertimeForRejectionTask;

    @Override
    public RejectOvertimeResponse execCommand(RejectOvertimeRequest request, JWTModel currentUser, String... args)
            throws Exception {
        String overtimeId = args.length > 0 ? args[0] : null;
        if (overtimeId == null) {
            throw new IllegalArgumentException("Overtime ID is required");
        }

        log.info("加班駁回流程開始: overtimeId={}", overtimeId);

        RejectOvertimeContext context = new RejectOvertimeContext(request, currentUser.getTenantId());
        context.setOvertimeId(overtimeId);

        BusinessPipeline.start(context)
                .next(loadOvertimeForRejectionTask)
                .next(performRejectOvertimeTask)
                .next(saveOvertimeForRejectionTask)
                .execute();

        log.info("加班駁回流程完成: overtimeId={}, status={}",
                context.getApplication().getId().getValue(),
                context.getApplication().getStatus());

        return RejectOvertimeResponse.success(context.getApplication().getId().getValue());
    }
}
