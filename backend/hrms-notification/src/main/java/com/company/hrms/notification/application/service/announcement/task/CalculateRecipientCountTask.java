package com.company.hrms.notification.application.service.announcement.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.announcement.context.AnnouncementContext;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;

import lombok.RequiredArgsConstructor;

/**
 * 計算收件人數量 Task (目前為基本實作，待整合 Organization Service)
 */
@Component
@RequiredArgsConstructor
public class CalculateRecipientCountTask implements PipelineTask<AnnouncementContext> {

    private final IAnnouncementRepository announcementRepository;

    @Override
    public void execute(AnnouncementContext context) {
        var request = context.getRequest();
        var announcement = context.getAnnouncement();

        // 實際應透過 Organization Service 查詢符合條件的員工總數
        // 目前暫時採用模擬計數邏輯
        int count;
        if (request.getTargetAudience() == null || "ALL".equals(request.getTargetAudience().getType())) {
            count = 500; // 模擬全員人數
        } else {
            count = 50; // 模擬特定範圍人數
        }

        context.setRecipientCount(count);
        announcement.setRecipientCount(count);

        // 更新公告中的收件人數（使用返回值保留版本號）
        var saved = announcementRepository.save(announcement);
        context.setAnnouncement(saved);
    }
}
