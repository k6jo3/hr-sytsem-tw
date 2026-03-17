package com.company.hrms.notification.application.service.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.notification.api.response.notification.GetNotificationsResponse;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.repository.INotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 通知列表查詢 Application Service（管理員用）
 * <p>
 * 提供根路徑列表端點，支援 recipientId、status 篩選與分頁。
 * Bean 名稱對應 Controller 方法名 getNotifications()
 * </p>
 *
 * @author Claude
 * @since 2026-03-16
 */
@Slf4j
@Service("getNotificationsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNotificationsServiceImpl
        implements QueryApiService<Void, GetNotificationsResponse> {

    private final INotificationRepository notificationRepository;

    @Override
    public GetNotificationsResponse getResponse(
            Void request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 解析查詢參數：recipientId, status, page, pageSize
        String recipientId = args.length > 0 ? args[0] : null;
        String status = args.length > 1 ? args[1] : null;
        int page = args.length > 2 && args[2] != null ? Integer.parseInt(args[2]) : 1;
        int pageSize = args.length > 3 && args[3] != null ? Integer.parseInt(args[3]) : 20;

        // 查詢通知列表
        List<Notification> notifications = notificationRepository.findAllNotifications(
                recipientId, status, page, pageSize);

        // 查詢總筆數
        long totalItems = notificationRepository.countAllNotifications(recipientId, status);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // 轉換為回應 DTO
        List<GetNotificationsResponse.NotificationItem> items = notifications.stream()
                .map(this::toNotificationItem)
                .collect(Collectors.toList());

        return GetNotificationsResponse.builder()
                .items(items)
                .pagination(GetNotificationsResponse.Pagination.builder()
                        .currentPage(page)
                        .pageSize(pageSize)
                        .totalItems(totalItems)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    /**
     * 將 Notification 聚合根轉換為列表項目 DTO
     */
    private GetNotificationsResponse.NotificationItem toNotificationItem(Notification notification) {
        return GetNotificationsResponse.NotificationItem.builder()
                .notificationId(notification.getId().getValue())
                .recipientId(notification.getRecipientId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .notificationType(notification.getNotificationType().name())
                .priority(notification.getPriority().name())
                .status(notification.getStatus().name())
                .isRead(notification.getReadAt() != null)
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
