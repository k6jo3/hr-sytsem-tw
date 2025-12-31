package com.company.hrms.attendance.application.service.overtime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.overtime.ApplyOvertimeRequest;
import com.company.hrms.attendance.api.response.overtime.ApplyOvertimeResponse;
import com.company.hrms.attendance.application.service.overtime.context.OvertimeContext;
import com.company.hrms.attendance.application.service.overtime.task.CreateOvertimeApplicationTask;
import com.company.hrms.attendance.application.service.overtime.task.SaveOvertimeApplicationTask;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 加班申請服務實作 (Pipeline 模式)
 * 
 * <p>
 * Pipeline 步驟：
 * <ol>
 * <li>CreateOvertimeApplicationTask - 建立加班申請聚合根</li>
 * <li>SaveOvertimeApplicationTask - 儲存加班申請</li>
 * </ol>
 * 
 * <p>
 * 注意：此服務未包含驗證 Task，因為 OvertimeApplication 聚合根建構子
 * 已內建時數驗證邏輯。若需要更複雜的驗證（如月加班時數上限檢查），
 * 可新增 ValidateOvertimeTask。
 */
@Service("applyOvertimeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplyOvertimeServiceImpl implements CommandApiService<ApplyOvertimeRequest, ApplyOvertimeResponse> {

        private final CreateOvertimeApplicationTask createOvertimeApplicationTask;
        private final SaveOvertimeApplicationTask saveOvertimeApplicationTask;

        @Override
        public ApplyOvertimeResponse execCommand(ApplyOvertimeRequest request, JWTModel currentUser, String... args)
                        throws Exception {
                log.info("加班申請流程開始: employeeId={}, date={}, hours={}",
                                request.getEmployeeId(), request.getDate(), request.getHours());

                OvertimeContext context = new OvertimeContext(request, currentUser.getTenantId());

                BusinessPipeline.start(context)
                                .next(createOvertimeApplicationTask)
                                .next(saveOvertimeApplicationTask)
                                .execute();

                log.info("加班申請流程完成: applicationId={}", context.getApplication().getId().getValue());

                return ApplyOvertimeResponse.success(context.getApplication().getId().getValue());
        }
}
