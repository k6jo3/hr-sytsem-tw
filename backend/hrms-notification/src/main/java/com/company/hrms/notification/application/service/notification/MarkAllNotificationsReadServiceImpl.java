package com.company.hrms.notification.application.service.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.response.notification.MarkAllReadResponse;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.repository.INotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 標記全部通知為已讀 Application Service
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@Service("markAllNotificationsReadServiceImpl")
@RequiredArgsConstructor
@Transactional
public class MarkAllNotificationsReadServiceImpl
                implements CommandApiService<Void, MarkAllReadResponse> {

        private final INotificationRepository notificationRepository;

        @Override
        public MarkAllReadResponse execCommand(
                        Void request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                String employeeId = currentUser.getEmployeeNumber();
                LocalDateTime readAt = LocalDateTime.now();

                // 查詢未讀通知
                List<Notification> unreadNotifications = notificationRepository.findUnreadByRecipientId(employeeId);

                // 逐一標記為已讀
                int markedCount = 0;
                for (Notification notification : unreadNotifications) {
                        notification.markAsRead();
                        notificationRepository.save(notification);
                        markedCount++;
                }

                log.info("全部通知已標記為已讀 - 使用者: {}, 已標記數量: {}",
                                employeeId, markedCount);

                return MarkAllReadResponse.builder()
                                .markedCount(markedCount)
                                .readAt(readAt)
                                .build();
        }
}
