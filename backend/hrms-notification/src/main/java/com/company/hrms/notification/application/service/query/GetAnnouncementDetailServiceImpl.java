package com.company.hrms.notification.application.service.query;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.notification.api.response.announcement.AnnouncementDetailResponse;
import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.model.valueobject.AnnouncementId;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢公告詳情 Application Service
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@Service("getAnnouncementDetailServiceImpl")
@RequiredArgsConstructor
public class GetAnnouncementDetailServiceImpl
                implements QueryApiService<Void, AnnouncementDetailResponse> {

        private final IAnnouncementRepository announcementRepository;

        @Override
        public AnnouncementDetailResponse getResponse(
                        Void request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                String announcementIdStr = args[0];

                // 查詢公告
                Announcement announcement = announcementRepository
                                .findById(AnnouncementId.of(announcementIdStr))
                                .orElseThrow(() -> new EntityNotFoundException("Announcement", announcementIdStr));

                // TODO: 驗證使用者是否有權限查看此公告（根據目標對象）

                // 組裝回應
                return AnnouncementDetailResponse.builder()
                                .announcementId(announcement.getId().getValue())
                                .title(announcement.getTitle())
                                .content(announcement.getContent())
                                .priority(announcement.getPriority().name())
                                .status(announcement.getStatus().name())
                                .targetAudience(AnnouncementDetailResponse.TargetAudience.builder()
                                                .type(announcement.getTargetAudience().name())
                                                .departmentIds(announcement.getTargetDepartmentIds())
                                                .roleIds(announcement.getTargetRoleIds())
                                                .build())
                                .attachments(announcement.getAttachments() != null
                                                ? announcement.getAttachments().stream()
                                                                .map(path -> AnnouncementDetailResponse.Attachment
                                                                                .builder()
                                                                                .fileName(path)
                                                                                .fileUrl(path)
                                                                                .build())
                                                                .collect(Collectors.toList())
                                                : Collections.emptyList())
                                .publishedAt(announcement.getPublishedAt())
                                .expireAt(announcement.getEffectiveTo())
                                .isRead(false) // TODO: 查詢使用者是否已讀
                                .publishedBy(AnnouncementDetailResponse.PublishedBy.builder()
                                                .employeeId(announcement.getPublishedBy())
                                                .fullName(announcement.getPublishedBy()) // TODO: 查詢員工姓名
                                                .build())
                                .createdAt(announcement.getCreatedAt())
                                .build();
        }
}
