package com.company.hrms.notification.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HR12NotificationCmdController API 測試
 * <p>
 * 測試範圍：
 * <ol>
 * <li>發送通知 API</li>
 * <li>批次發送通知 API</li>
 * <li>測試發送通知 API</li>
 * <li>API 權限驗證</li>
 * <li>請求參數驗證</li>
 * </ol>
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("HR12NotificationCmdController API 測試")
class HR12NotificationCmdControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @org.springframework.boot.test.context.TestConfiguration
        static class MockConfig {
                @org.springframework.context.annotation.Bean
                public org.springframework.messaging.simp.SimpMessagingTemplate simpMessagingTemplate() {
                        return org.mockito.Mockito.mock(org.springframework.messaging.simp.SimpMessagingTemplate.class);
                }

                @org.springframework.context.annotation.Bean
                public org.springframework.mail.javamail.JavaMailSender javaMailSender() {
                        return org.mockito.Mockito.mock(org.springframework.mail.javamail.JavaMailSender.class);
                }

                @org.springframework.context.annotation.Bean
                public org.springframework.web.client.RestTemplate restTemplate() {
                        return org.mockito.Mockito.mock(org.springframework.web.client.RestTemplate.class);
                }
        }

        @Test
        @DisplayName("API 1: POST /api/v1/notifications/send - 應該成功發送通知")
        @WithMockUser(authorities = { "NOTIFICATION:SEND" })
        void testSendNotification_ShouldSucceed() throws Exception {
                // Given: 準備請求
                SendNotificationRequest request = SendNotificationRequest.builder()
                                .recipientId("emp-001")
                                .title("系統維護通知")
                                .content("系統將於今晚 22:00-24:00 進行維護。")
                                .notificationType("SYSTEM_NOTICE")
                                .channels(List.of("IN_APP"))
                                .priority("NORMAL")
                                .build();

                // When & Then: 執行請求並驗證
                mockMvc.perform(post("/api/v1/notifications/send")
                                .with(csrf())
                                .with(authentication(new UsernamePasswordAuthenticationToken(
                                                JWTModel.builder().employeeNumber("emp-001").tenantId("tenant-001")
                                                                .build(),
                                                null,
                                                List.of(new SimpleGrantedAuthority("NOTIFICATION:SEND")))))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.notificationId").exists())
                                .andExpect(jsonPath("$.recipientId").value("emp-001"))
                                .andExpect(jsonPath("$.title").value("系統維護通知"))
                                .andExpect(jsonPath("$.status").exists())
                                .andExpect(jsonPath("$.channels").isArray())
                                .andExpect(jsonPath("$.sentAt").exists());
        }

        @Test
        @DisplayName("API 1: POST /api/v1/notifications/send - 缺少必填欄位應回傳 400")
        @WithMockUser(authorities = { "NOTIFICATION:SEND" })
        void testSendNotification_MissingRequiredFields_ShouldReturn400() throws Exception {
                // Given: 缺少 recipientId 的請求
                SendNotificationRequest request = SendNotificationRequest.builder()
                                // .recipientId("emp-001") // 缺少必填欄位
                                .title("測試通知")
                                .content("測試內容")
                                .notificationType("SYSTEM_NOTICE")
                                .build();

                // When & Then: 應該回傳 400
                mockMvc.perform(post("/api/v1/notifications/send")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("API 1: POST /api/v1/notifications/send - 無權限應回傳 403")
        @WithMockUser(authorities = { "NOTIFICATION:READ" }) // 無 SEND 權限
        void testSendNotification_NoPermission_ShouldReturn403() throws Exception {
                // Given: 準備請求
                SendNotificationRequest request = SendNotificationRequest.builder()
                                .recipientId("emp-001")
                                .title("測試通知")
                                .content("測試內容")
                                .notificationType("SYSTEM_NOTICE")
                                .build();

                // When & Then: 應該回傳 403
                mockMvc.perform(post("/api/v1/notifications/send")
                                .with(csrf())
                                .with(authentication(new UsernamePasswordAuthenticationToken(
                                                JWTModel.builder().employeeNumber("no-perm-user").tenantId("tenant-001")
                                                                .build(),
                                                null,
                                                List.of(new SimpleGrantedAuthority("ROLE_USER"))))) // 無
                                                                                                    // NOTIFICATION:SEND
                                                                                                    // 權限
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("API 2: POST /api/v1/notifications/send-batch - 應該成功批次發送")
        @WithMockUser(authorities = { "NOTIFICATION:SEND_BATCH" })
        void testSendBatchNotification_ShouldSucceed() throws Exception {
                // Given: 準備批次請求
                String requestJson = """
                                {
                                    "recipientIds": ["emp-001", "emp-002", "emp-003"],
                                    "title": "重要公告",
                                    "content": "公司將於下週一實施新制度。",
                                    "notificationType": "ANNOUNCEMENT",
                                    "channels": ["IN_APP", "EMAIL"],
                                    "priority": "HIGH"
                                }
                                """;

                // When & Then: 執行請求並驗證
                mockMvc.perform(post("/api/v1/notifications/send-batch")
                                .with(csrf())
                                .with(authentication(new UsernamePasswordAuthenticationToken(
                                                JWTModel.builder().employeeNumber("emp-001").tenantId("tenant-001")
                                                                .build(),
                                                null,
                                                List.of(new SimpleGrantedAuthority("NOTIFICATION:SEND_BATCH")))))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalRecipients").value(3))
                                .andExpect(jsonPath("$.successCount").exists())
                                .andExpect(jsonPath("$.failureCount").exists())
                                .andExpect(jsonPath("$.results").isArray());
        }

        @Test
        @DisplayName("API 3: POST /api/v1/notifications/test - 應該發送測試通知給當前使用者")
        @WithMockUser(authorities = { "NOTIFICATION:SEND" })
        void testSendTestNotification_ShouldSucceed() throws Exception {
                // Given: 準備測試請求
                SendNotificationRequest request = SendNotificationRequest.builder()
                                .recipientId("any-recipient") // 會被覆蓋為當前使用者
                                .title("測試通知標題")
                                .content("測試通知內容")
                                .notificationType("SYSTEM_NOTICE")
                                .priority("NORMAL")
                                .build();

                // When & Then: 執行請求並驗證
                mockMvc.perform(post("/api/v1/notifications/test")
                                .with(csrf())
                                .with(authentication(new UsernamePasswordAuthenticationToken(
                                                JWTModel.builder().employeeNumber("emp-001").tenantId("tenant-001")
                                                                .build(),
                                                null,
                                                List.of(new SimpleGrantedAuthority("NOTIFICATION:SEND")))))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.notificationId").exists())
                                .andExpect(jsonPath("$.title").value("[測試] 測試通知標題"))
                                .andExpect(jsonPath("$.channels").isArray())
                                .andExpect(jsonPath("$.channels[0]").value("IN_APP")); // 強制只使用 IN_APP
        }

        @Test
        @DisplayName("API: 所有端點未認證應回傳 401")
        void testAllEndpoints_NoAuth_ShouldReturn401() throws Exception {
                // Test 1: /send
                mockMvc.perform(post("/api/v1/notifications/send")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isUnauthorized());

                // Test 2: /send-batch
                mockMvc.perform(post("/api/v1/notifications/send-batch")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isUnauthorized());

                // Test 3: /test
                mockMvc.perform(post("/api/v1/notifications/test")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isUnauthorized());
        }
}
