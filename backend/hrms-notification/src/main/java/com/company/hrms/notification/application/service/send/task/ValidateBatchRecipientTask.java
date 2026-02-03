package com.company.hrms.notification.application.service.send.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendBatchNotificationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 驗證批次收件人 Task
 * <p>
 * 職責：
 * <ol>
 * <li>確保收件人列表不為空</li>
 * <li>確保收件人數量不超過系統限制 (500人)</li>
 * </ol>
 * </p>
 */
@Slf4j
@Component
public class ValidateBatchRecipientTask implements PipelineTask<SendBatchNotificationContext> {

    private static final int MAX_RECIPIENTS = 500;

    @Override
    public void execute(SendBatchNotificationContext context) {
        List<String> ids = context.getRecipientIds();

        // 驗證收件人數量
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("收件人列表不可為空");
        }

        if (ids.size() > MAX_RECIPIENTS) {
            throw new IllegalArgumentException(
                    String.format("收件人數量超過上限（最多 %d 人，實際 %d 人）",
                            MAX_RECIPIENTS, ids.size()));
        }

        log.info("[ValidateBatchRecipientTask] 收件人驗證通過，共 {} 人", ids.size());
    }
}
