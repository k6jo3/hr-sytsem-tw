package com.company.hrms.notification.application.service.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.notification.api.response.announcement.AnnouncementListResponse;
import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;

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

                // 過濾並轉換
                List<AnnouncementListResponse.AnnouncementItem> items = announcements.stream()
                                .filter(a -> !a.isWithdrawn())
                                .map(this::toAnnouncementItem)
                                .skip((long) (page - 1) * pageSize)
                                .limit(pageSize)
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

        private AnnouncementListResponse.AnnouncementItem toAnnouncementItem(Announcement announcement) {
                return AnnouncementListResponse.AnnouncementItem.builder()
                                .announcementId(announcement.getId().getValue())
                                .title(announcement.getTitle())
                                .summary(announcement.getContent() != null && announcement.getContent().length() > 100
                                                ? announcement.getContent().substring(0, 100) + "..."
                                                : announcement.getContent())
                                .priority(announcement.getPriority().name())
                                .status(announcement.getStatus().name())
                                .isPinned(announcement.isPinned())
                                .isRead(false) // TODO: 查詢使用者是否已讀
                                .publishedAt(announcement.getPublishedAt())
                                .expireAt(announcement.getEffectiveTo())
                                .publishedBy(AnnouncementListResponse.PublishedBy.builder()
                                                .employeeId(announcement.getPublishedBy())
                                                .fullName(announcement.getPublishedBy()) // TODO: 查詢員工姓名
                                                .build())
                                .build();
        }
}
