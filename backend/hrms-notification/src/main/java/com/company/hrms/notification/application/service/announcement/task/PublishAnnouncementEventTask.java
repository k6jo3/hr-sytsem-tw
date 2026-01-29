package com.company.hrms.notification.application.service.announcement.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.notification.application.service.announcement.context.AnnouncementContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布公告相關領域事件 Task
 * <p>
 * 職責：發布公告聚合根中註冊的所有領域事件
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PublishAnnouncementEventTask implements PipelineTask<AnnouncementContext> {

    private final EventPublisher eventPublisher;

    @Override
    public void execute(AnnouncementContext context) {
        var announcement = context.getAnnouncement();
        if (announcement == null) {
            return;
        }

        var events = announcement.getDomainEvents();
        if (!events.isEmpty()) {
            log.info("[PublishAnnouncementEventTask] 開始發布領域事件，數量: {}", events.size());
            eventPublisher.publishAll(events);
            announcement.clearDomainEvents();
            log.info("[PublishAnnouncementEventTask] 領域事件發布完成");
        }
    }
}
