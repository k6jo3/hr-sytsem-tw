package com.company.hrms.notification.application.service.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.notification.api.response.notification.GetMyNotificationsResponse;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.repository.INotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢我的通知列表 Application Service
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@Service("getMyNotificationsServiceImpl")
@RequiredArgsConstructor
public class GetMyNotificationsServiceImpl
                implements QueryApiService<Void, GetMyNotificationsResponse> {

        private final INotificationRepository notificationRepository;

        @Override
        public GetMyNotificationsResponse getResponse(
                        Void request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                String recipientId = currentUser.getEmployeeNumber();

                // 解析分頁參數
                int page = args.length > 4 && args[4] != null ? Integer.parseInt(args[4]) : 1;
                int pageSize = args.length > 5 && args[5] != null ? Integer.parseInt(args[5]) : 20;

                // 查詢通知列表（簡化實作：查詢全部後轉換）
                List<Notification> notifications = notificationRepository.findByRecipientId(recipientId);

                // 轉換為回應項目
                List<GetMyNotificationsResponse.NotificationItem> items = notifications.stream()
                                .map(this::toNotificationItem)
                                .skip((long) (page - 1) * pageSize)
                                .limit(pageSize)
                                .collect(Collectors.toList());

                // 查詢未讀數量
                long unreadCount = notificationRepository.countUnreadByRecipientId(recipientId);

                return GetMyNotificationsResponse.builder()
                                .items(items)
                                .pagination(GetMyNotificationsResponse.Pagination.builder()
                                                .currentPage(page)
                                                .pageSize(pageSize)
                                                .totalItems((long) notifications.size())
                                                .totalPages((int) Math.ceil((double) notifications.size() / pageSize))
                                                .build())
                                .summary(GetMyNotificationsResponse.Summary.builder()
                                                .totalUnread(unreadCount)
                                                .build())
                                .build();
        }

        private GetMyNotificationsResponse.NotificationItem toNotificationItem(Notification notification) {
                return GetMyNotificationsResponse.NotificationItem.builder()
                                .notificationId(notification.getId().getValue())
                                .title(notification.getTitle())
                                .content(notification.getContent())
                                .notificationType(notification.getNotificationType().name())
                                .priority(notification.getPriority().name())
                                .status(notification.getStatus().name())
                                .isRead(notification.getReadAt() != null)
                                .createdAt(notification.getCreatedAt())
                                .build();
        }
}
