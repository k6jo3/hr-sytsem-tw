package com.company.hrms.notification.application.service.announcement;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.request.announcement.UpdateAnnouncementRequest;
import com.company.hrms.notification.api.response.announcement.UpdateAnnouncementResponse;
import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.model.valueobject.AnnouncementId;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新公告 Application Service
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@Service("updateAnnouncementServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateAnnouncementServiceImpl
        implements CommandApiService<UpdateAnnouncementRequest, UpdateAnnouncementResponse> {

    private final IAnnouncementRepository announcementRepository;

    @Override
    public UpdateAnnouncementResponse execCommand(
            UpdateAnnouncementRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 取得公告 ID（從 args 傳入）
        String announcementIdStr = args[0];

        // 查詢公告
        Announcement announcement = announcementRepository
                .findById(AnnouncementId.of(announcementIdStr))
                .orElseThrow(() -> new EntityNotFoundException("Announcement", announcementIdStr));

        // 轉換優先級
        NotificationPriority priority = null;
        if (request.getPriority() != null) {
            priority = NotificationPriority.valueOf(request.getPriority());
        }

        // 更新公告
        announcement.updateContent(
                request.getTitle(),
                request.getContent(),
                priority,
                request.getExpireAt());

        // 儲存
        announcementRepository.save(announcement);

        log.info("公告已更新 - 公告ID: {}", announcementIdStr);

        return UpdateAnnouncementResponse.builder()
                .announcementId(announcementIdStr)
                .title(announcement.getTitle())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
