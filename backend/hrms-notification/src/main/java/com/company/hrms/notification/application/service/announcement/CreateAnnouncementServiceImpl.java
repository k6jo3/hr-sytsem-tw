package com.company.hrms.notification.application.service.announcement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.request.announcement.CreateAnnouncementRequest;
import com.company.hrms.notification.api.response.announcement.CreateAnnouncementResponse;
import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布公告 Application Service
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@Service("createAnnouncementServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CreateAnnouncementServiceImpl
                implements CommandApiService<CreateAnnouncementRequest, CreateAnnouncementResponse> {

        private final IAnnouncementRepository announcementRepository;

        @Override
        public CreateAnnouncementResponse execCommand(
                        CreateAnnouncementRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                // 決定發布時間
                LocalDateTime publishAt = request.getPublishAt() != null
                                ? request.getPublishAt()
                                : LocalDateTime.now();

                // 轉換優先級
                NotificationPriority priority = NotificationPriority.NORMAL;
                if (request.getPriority() != null) {
                        priority = NotificationPriority.valueOf(request.getPriority());
                }

                // 轉換渠道
                List<NotificationChannel> channels = null;
                if (request.getChannels() != null && !request.getChannels().isEmpty()) {
                        channels = request.getChannels().stream()
                                        .map(NotificationChannel::valueOf)
                                        .collect(Collectors.toList());
                }

                // 轉換目標對象類型
                Announcement.TargetAudience targetAudience = Announcement.TargetAudience.ALL;
                if (request.getTargetAudience() != null && request.getTargetAudience().getType() != null) {
                        targetAudience = Announcement.TargetAudience.valueOf(request.getTargetAudience().getType());
                }

                // 建立 Announcement 聚合根
                Announcement announcement = Announcement.create(
                                request.getTitle(),
                                request.getContent(),
                                priority,
                                channels,
                                targetAudience,
                                currentUser.getEmployeeNumber(),
                                publishAt,
                                request.getExpireAt());

                // 設定目標對象
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

                // 儲存公告
                announcementRepository.save(announcement);

                // TODO: 根據目標對象發送通知
                int recipientCount = calculateRecipientCount(request);

                log.info("公告已發布 - 公告ID: {}, 標題: {}, 收件人數: {}",
                                announcement.getId().getValue(), request.getTitle(), recipientCount);

                return CreateAnnouncementResponse.builder()
                                .announcementId(announcement.getId().getValue())
                                .title(request.getTitle())
                                .status("PUBLISHED")
                                .recipientCount(recipientCount)
                                .publishedAt(publishAt)
                                .expireAt(request.getExpireAt())
                                .build();
        }

        /**
         * 計算收件人數量（簡化實作）
         */
        private int calculateRecipientCount(CreateAnnouncementRequest request) {
                // TODO: 實際應查詢員工數量
                if (request.getTargetAudience() == null || "ALL".equals(request.getTargetAudience().getType())) {
                        return 500; // 假設全員 500 人
                }
                return 50; // 假設部門/角色 50 人
        }
}
