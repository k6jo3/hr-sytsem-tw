package com.company.hrms.notification.application.service.announcement.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.announcement.context.AnnouncementContext;
import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;

import lombok.RequiredArgsConstructor;

/**
 * 建立並儲存公告 Task
 */
@Component
@RequiredArgsConstructor
public class CreateAnnouncementTask implements PipelineTask<AnnouncementContext> {

    private final IAnnouncementRepository announcementRepository;

    @Override
    public void execute(AnnouncementContext context) {
        var request = context.getRequest();

        // 1. 決定發布時間
        LocalDateTime publishAt = request.getPublishAt() != null
                ? request.getPublishAt()
                : LocalDateTime.now();

        // 2. 轉換優先級
        NotificationPriority priority = NotificationPriority.NORMAL;
        if (request.getPriority() != null) {
            priority = NotificationPriority.valueOf(request.getPriority());
        }

        // 3. 轉換渠道
        List<NotificationChannel> channels = null;
        if (request.getChannels() != null && !request.getChannels().isEmpty()) {
            channels = request.getChannels().stream()
                    .map(NotificationChannel::valueOf)
                    .collect(Collectors.toList());
        }

        // 4. 轉換目標對象類型
        Announcement.TargetAudience targetAudience = Announcement.TargetAudience.ALL;
        if (request.getTargetAudience() != null && request.getTargetAudience().getType() != null) {
            targetAudience = Announcement.TargetAudience.valueOf(request.getTargetAudience().getType());
        }

        // 5. 建立 Announcement 聚合根
        Announcement announcement = Announcement.create(
                request.getTitle(),
                request.getContent(),
                priority,
                channels,
                targetAudience,
                context.getPublishedBy(),
                publishAt,
                request.getExpireAt());

        // 6. 設定目標對象詳情
        if (request.getTargetAudience() != null) {
            if (request.getTargetAudience().getDepartmentIds() != null
                    && !request.getTargetAudience().getDepartmentIds().isEmpty()) {
                announcement.setTargetDepartments(request.getTargetAudience().getDepartmentIds());
            }
            if (request.getTargetAudience().getRoleIds() != null
                    && !request.getTargetAudience().getRoleIds().isEmpty()) {
                announcement.setTargetRoles(request.getTargetAudience().getRoleIds());
            }
        }

        // 7. 儲存公告（使用返回值以取得版本號，避免後續 save 樂觀鎖衝突）
        Announcement saved = announcementRepository.save(announcement);
        context.setAnnouncement(saved);
    }
}
