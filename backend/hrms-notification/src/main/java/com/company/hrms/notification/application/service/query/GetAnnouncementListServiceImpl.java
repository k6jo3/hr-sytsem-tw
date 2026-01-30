package com.company.hrms.notification.application.service.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.notification.api.response.announcement.AnnouncementListResponse;
import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.repository.IAnnouncementReadRecordRepository;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;
import com.company.hrms.notification.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.notification.infrastructure.client.organization.dto.EmployeeDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢公告列表 Application Service
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@Service("getAnnouncementListServiceImpl")
@RequiredArgsConstructor
public class GetAnnouncementListServiceImpl
                implements QueryApiService<Void, AnnouncementListResponse> {

        private final IAnnouncementRepository announcementRepository;
        private final IAnnouncementReadRecordRepository readRecordRepository;
        private final OrganizationServiceClient organizationServiceClient;

        @Override
        public AnnouncementListResponse getResponse(
                        Void request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                // 解析參數
                int page = args.length > 1 && args[1] != null ? Integer.parseInt(args[1]) : 1;
                int pageSize = args.length > 2 && args[2] != null ? Integer.parseInt(args[2]) : 20;

                // 查詢公告列表（簡化實作：查詢全部可看公告）
                List<Announcement> announcements = announcementRepository.findAll();

                // 查詢使用者已讀記錄
                Set<String> readAnnouncementIds = readRecordRepository
                                .findReadAnnouncementIdsByEmployeeId(currentUser.getUserId());

                // 準備員工姓名快取
                Map<String, String> employeeNameCache = new HashMap<>();

                // 過濾並轉換
                List<AnnouncementListResponse.AnnouncementItem> items = announcements.stream()
                                .filter(a -> !a.isWithdrawn())
                                .filter(a -> hasPermission(a, currentUser))
                                .skip((long) (page - 1) * pageSize)
                                .limit(pageSize)
                                .map(a -> toAnnouncementItem(a, readAnnouncementIds, employeeNameCache))
                                .collect(Collectors.toList());

                int totalCount = (int) announcements.stream().filter(a -> !a.isWithdrawn()).count();

                return AnnouncementListResponse.builder()
                                .items(items)
                                .pagination(AnnouncementListResponse.Pagination.builder()
                                                .currentPage(page)
                                                .pageSize(pageSize)
                                                .totalItems((long) totalCount)
                                                .totalPages((int) Math.ceil((double) totalCount / pageSize))
                                                .build())
                                .build();
        }

        private AnnouncementListResponse.AnnouncementItem toAnnouncementItem(
                        Announcement announcement,
                        Set<String> readAnnouncementIds,
                        Map<String, String> employeeNameCache) {

                String publishedBy = announcement.getPublishedBy();
                String publishedByName = employeeNameCache.computeIfAbsent(publishedBy, this::fetchEmployeeName);

                return AnnouncementListResponse.AnnouncementItem.builder()
                                .announcementId(announcement.getId().getValue())
                                .title(announcement.getTitle())
                                .summary(announcement.getContent() != null && announcement.getContent().length() > 100
                                                ? announcement.getContent().substring(0, 100) + "..."
                                                : announcement.getContent())
                                .priority(announcement.getPriority().name())
                                .status(announcement.getStatus().name())
                                .isPinned(announcement.isPinned())
                                .isRead(readAnnouncementIds.contains(announcement.getId().getValue()))
                                .publishedAt(announcement.getPublishedAt())
                                .expireAt(announcement.getEffectiveTo())
                                .publishedBy(AnnouncementListResponse.PublishedBy.builder()
                                                .employeeId(publishedBy)
                                                .fullName(publishedByName)
                                                .build())
                                .build();
        }

        private String fetchEmployeeName(String employeeId) {
                try {
                        EmployeeDto employee = organizationServiceClient.getEmployeeDetail(employeeId);
                        return employee != null ? employee.getFullName() : employeeId;
                } catch (Exception e) {
                        log.warn("無法查詢員工姓名: {}", employeeId);
                        return employeeId;
                }
        }

        private boolean hasPermission(Announcement announcement, JWTModel currentUser) {
                // 如果發布者是自己
                if (currentUser.getUserId().equals(announcement.getPublishedBy())) {
                        return true;
                }

                // 如果是全員公告
                if (announcement.getTargetAudience() == Announcement.TargetAudience.ALL) {
                        return true;
                }

                // 如果是特定部門公告
                if (announcement.getTargetAudience() == Announcement.TargetAudience.DEPARTMENT) {
                        return announcement.getTargetDepartmentIds() != null &&
                                        announcement.getTargetDepartmentIds().contains(currentUser.getDepartmentId());
                }

                // 如果是特定角色公告
                if (announcement.getTargetAudience() == Announcement.TargetAudience.ROLE) {
                        if (announcement.getTargetRoleIds() != null && currentUser.getRoles() != null) {
                                for (String role : currentUser.getRoles()) {
                                        if (announcement.getTargetRoleIds().contains(role)) {
                                                return true;
                                        }
                                }
                        }
                        return false;
                }

                // 如果是特定員工公告
                if (announcement.getTargetAudience() == Announcement.TargetAudience.SPECIFIC) {
                        return announcement.getTargetEmployeeIds() != null &&
                                        announcement.getTargetEmployeeIds().contains(currentUser.getUserId());
                }

                return false;
        }
}
