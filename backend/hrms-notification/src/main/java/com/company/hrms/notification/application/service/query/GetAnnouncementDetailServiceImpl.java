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
import com.company.hrms.notification.domain.repository.IAnnouncementReadRecordRepository;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;
import com.company.hrms.notification.infrastructure.client.organization.OrganizationServiceClient;

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
        private final OrganizationServiceClient organizationServiceClient;
        private final IAnnouncementReadRecordRepository readRecordRepository;

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

                // 1. 驗證使用者是否有權限查看此公告（根據目標對象）
                validatePermission(announcement, currentUser);

                // 2. 查詢發布者姓名
                String publishedByName = announcement.getPublishedBy();
                try {
                        var employee = organizationServiceClient.getEmployeeDetail(announcement.getPublishedBy());
                        if (employee != null) {
                                publishedByName = employee.getFullName();
                        }
                } catch (Exception e) {
                        log.warn("無法取得發布者姓名: {}", e.getMessage());
                }

                // 3. 查詢是否已讀
                boolean isRead = readRecordRepository.existsByAnnouncementIdAndEmployeeId(
                                announcementIdStr, currentUser.getUserId());

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
                                .isRead(isRead)
                                .publishedBy(AnnouncementDetailResponse.PublishedBy.builder()
                                                .employeeId(announcement.getPublishedBy())
                                                .fullName(publishedByName)
                                                .build())
                                .createdAt(announcement.getCreatedAt())
                                .build();
        }

        private void validatePermission(Announcement announcement, JWTModel currentUser) {
                // 如果是全員公告，所有人都有權限
                if (announcement.getTargetAudience() == Announcement.TargetAudience.ALL) {
                        return;
                }

                // 如果是特定部門公告
                if (announcement.getTargetAudience() == Announcement.TargetAudience.DEPARTMENT) {
                        if (announcement.getTargetDepartmentIds() != null &&
                                        announcement.getTargetDepartmentIds().contains(currentUser.getDepartmentId())) {
                                return;
                        }
                }

                // 如果是特定角色公告
                if (announcement.getTargetAudience() == Announcement.TargetAudience.ROLE) {
                        if (announcement.getTargetRoleIds() != null && currentUser.getRoles() != null) {
                                for (String role : currentUser.getRoles()) {
                                        if (announcement.getTargetRoleIds().contains(role)) {
                                                return;
                                        }
                                }
                        }
                }

                // 如果是特定員工公告
                if (announcement.getTargetAudience() == Announcement.TargetAudience.SPECIFIC) {
                        if (announcement.getTargetEmployeeIds() != null &&
                                        announcement.getTargetEmployeeIds().contains(currentUser.getUserId())) {
                                return;
                        }
                }

                // 如果發布者是自己
                if (currentUser.getUserId().equals(announcement.getPublishedBy())) {
                        return;
                }

                throw new IllegalArgumentException("您無權查看此公告");
        }
}
