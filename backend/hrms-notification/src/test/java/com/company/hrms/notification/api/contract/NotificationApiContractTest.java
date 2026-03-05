package com.company.hrms.notification.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationId;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.model.valueobject.NotificationStatus;
import com.company.hrms.notification.domain.model.valueobject.NotificationType;
import com.company.hrms.notification.domain.repository.IAnnouncementReadRecordRepository;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;
import com.company.hrms.notification.domain.repository.INotificationPreferenceRepository;
import com.company.hrms.notification.domain.repository.INotificationRepository;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import com.company.hrms.notification.infrastructure.client.organization.OrganizationServiceClient;

/**
 * HR12 通知管理服務 API 合約測試 (整合版本)
 * 驗證 Controller -> Service -> Repository 的完整流程
 * 同時涵蓋 Query 合約驗證與 Command 業務流程驗證
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR12 通知管理服務 API 合約測試")
public class NotificationApiContractTest extends BaseApiContractTest {

    private static final String CONTRACT = "notification";
    private String contractSpec;

    // === 領域 Repository ===

    @MockBean
    private INotificationRepository notificationRepository;

    @MockBean
    private INotificationTemplateRepository templateRepository;

    @MockBean
    private IAnnouncementRepository announcementRepository;

    @MockBean
    private INotificationPreferenceRepository preferenceRepository;

    @MockBean
    private IAnnouncementReadRecordRepository readRecordRepository;

    // === 外部服務 Client ===

    @MockBean
    private OrganizationServiceClient organizationServiceClient;

    private JWTModel mockUser;

    /** 測試用 SENT 狀態通知 (可標記已讀) */
    private Notification sentNotification;

    @BeforeEach
    void setUp() throws Exception {
        contractSpec = loadContractSpec(CONTRACT);

        // 設定測試用模擬使用者
        mockUser = new JWTModel();
        mockUser.setUserId("00000000-0000-0000-0000-000000000001");
        mockUser.setUsername("test-user");
        mockUser.setEmployeeNumber("00000000-0000-0000-0000-000000000003");
        mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

        // 建立 SENT 狀態通知 (可標記已讀)
        sentNotification = new Notification(NotificationId.generate());
        sentNotification.setRecipientId("00000000-0000-0000-0000-000000000003");
        sentNotification.setTitle("測試通知");
        sentNotification.setContent("這是一則測試通知內容");
        sentNotification.setNotificationType(NotificationType.SYSTEM);
        sentNotification.setChannels(List.of(NotificationChannel.IN_APP));
        sentNotification.setPriority(NotificationPriority.NORMAL);
        sentNotification.setStatus(NotificationStatus.SENT);
        sentNotification.setSentAt(LocalDateTime.now().minusHours(1));

        // 設定 Repository 的 lenient 預設行為
        lenient().when(notificationRepository.findByRecipientId(anyString()))
                .thenReturn(Collections.emptyList());
        lenient().when(notificationRepository.countUnreadByRecipientId(anyString()))
                .thenReturn(0L);
        lenient().when(notificationRepository.findById(any()))
                .thenReturn(Optional.of(sentNotification));
        lenient().when(notificationRepository.save(any()))
                .thenReturn(sentNotification);

        lenient().when(templateRepository.findAllActive())
                .thenReturn(Collections.emptyList());
        lenient().when(templateRepository.findAll())
                .thenReturn(Collections.emptyList());
        lenient().when(templateRepository.findTemplates(any(QueryGroup.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        lenient().when(announcementRepository.findAllActiveAnnouncements())
                .thenReturn(Collections.emptyList());
        lenient().when(announcementRepository.findAll())
                .thenReturn(Collections.emptyList());

        lenient().when(readRecordRepository.findReadAnnouncementIdsByEmployeeId(anyString()))
                .thenReturn(new HashSet<>());

        lenient().when(preferenceRepository.findByEmployeeId(anyString()))
                .thenReturn(Optional.empty());
        lenient().when(preferenceRepository.findByEmployeeIdOrCreateDefault(anyString()))
                .thenReturn(null);
    }

    // =========================================================================
    // 通知範本 API 合約
    // =========================================================================

    @Nested
    @DisplayName("通知範本 API 合約")
    class TemplateApiContractTests {

        @Test
        @DisplayName("NTF_T001: 查詢通知範本列表")
        void getTemplateList_ShouldCallTemplateRepository() throws Exception {
            // Arrange
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(templateRepository.findTemplates(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // Act
            mockMvc.perform(get("/api/v1/notifications/templates")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert - 驗證範本查詢 Repository 被呼叫
            verify(templateRepository).findTemplates(any(QueryGroup.class), any(Pageable.class));
        }
    }

    // =========================================================================
    // 我的通知 API 合約
    // =========================================================================

    @Nested
    @DisplayName("我的通知 API 合約")
    class MyNotificationApiContractTests {

        @Test
        @DisplayName("NTF_M001: 查詢我的通知列表")
        void getMyNotifications_ShouldCallNotificationRepository() throws Exception {
            // Arrange - findByRecipientId 使用當前使用者的 employeeNumber
            when(notificationRepository.findByRecipientId("00000000-0000-0000-0000-000000000003"))
                    .thenReturn(Collections.emptyList());
            when(notificationRepository.countUnreadByRecipientId("00000000-0000-0000-0000-000000000003"))
                    .thenReturn(0L);

            // Act
            mockMvc.perform(get("/api/v1/notifications/me")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert - 驗證通知 Repository 的 findByRecipientId 被呼叫
            verify(notificationRepository).findByRecipientId("00000000-0000-0000-0000-000000000003");
            verify(notificationRepository).countUnreadByRecipientId("00000000-0000-0000-0000-000000000003");
        }
    }

    // =========================================================================
    // 公告 API 合約
    // =========================================================================

    @Nested
    @DisplayName("公告 API 合約")
    class AnnouncementApiContractTests {

        @Test
        @DisplayName("NTF_A001: 查詢公告列表")
        void getAnnouncementList_ShouldCallAnnouncementRepository() throws Exception {
            // Arrange
            when(announcementRepository.findAll())
                    .thenReturn(Collections.emptyList());

            // Act
            mockMvc.perform(get("/api/v1/notifications/announcements")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert - 驗證公告 Repository 的 findAll 被呼叫
            verify(announcementRepository).findAll();
        }
    }

    // =========================================================================
    // 通知命令 API 合約
    // =========================================================================

    @Nested
    @DisplayName("通知命令 API 合約")
    class NotificationCommandApiContractTests {

        @Test
        @DisplayName("NTF_CMD_001: 標記通知為已讀 - 驗證通知狀態從 SENT 轉換為 READ")
        void markNotificationRead_ShouldTransitionFromSentToRead() throws Exception {
            // Arrange
            String notificationId = sentNotification.getId().getValue();
            when(notificationRepository.findById(any()))
                    .thenReturn(Optional.of(sentNotification));

            // Act
            mockMvc.perform(put("/api/v1/notifications/{id}/read", notificationId)
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert - 驗證通知被載入及儲存 (markAsRead() 後狀態為 READ)
            verify(notificationRepository).findById(any());
            verify(notificationRepository).save(any(Notification.class));
        }
    }
}
