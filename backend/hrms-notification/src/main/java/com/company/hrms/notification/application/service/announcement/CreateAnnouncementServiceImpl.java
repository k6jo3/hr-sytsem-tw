package com.company.hrms.notification.application.service.announcement;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.request.announcement.CreateAnnouncementRequest;
import com.company.hrms.notification.api.response.announcement.CreateAnnouncementResponse;
import com.company.hrms.notification.application.service.announcement.context.AnnouncementContext;
import com.company.hrms.notification.application.service.announcement.task.CalculateRecipientCountTask;
import com.company.hrms.notification.application.service.announcement.task.CreateAnnouncementTask;
import com.company.hrms.notification.application.service.announcement.task.PublishAnnouncementEventTask;

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

        private final CreateAnnouncementTask createAnnouncementTask;
        private final CalculateRecipientCountTask calculateRecipientCountTask;
        private final PublishAnnouncementEventTask publishAnnouncementEventTask;

        @Override
        public CreateAnnouncementResponse execCommand(
                        CreateAnnouncementRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                log.info("使用者 {} 發布公告: {}", currentUser.getEmployeeNumber(), request.getTitle());

                // 1. 建立 Context
                AnnouncementContext context = new AnnouncementContext(request, currentUser.getEmployeeNumber());

                // 2. 執行 Pipeline
                com.company.hrms.common.application.pipeline.BusinessPipeline.start(context)
                                .next(createAnnouncementTask)
                                .next(calculateRecipientCountTask)
                                .next(publishAnnouncementEventTask)
                                .execute();

                // 3. 建立回應
                var announcement = context.getAnnouncement();

                return CreateAnnouncementResponse.builder()
                                .announcementId(announcement.getId().getValue())
                                .title(announcement.getTitle())
                                .status(announcement.getStatus().name())
                                .recipientCount(context.getRecipientCount())
                                .publishedAt(announcement.getPublishedAt())
                                .expireAt(announcement.getEffectiveTo())
                                .build();
        }
}
