package com.company.hrms.iam.api.contract;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.event.InMemoryEventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.common.test.contract.ContractSpec;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * IAM API 合約測試（整合測試）
 *
 * 使用真實資料庫進行完整的合約驗證，包含資料異動和領域事件
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-data/iam_base_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class IamContractTest extends BaseContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestEventCaptor eventCaptor;

    private String contractSpec;
    private JWTModel mockAdminUser;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
            return new InMemoryEventPublisher(applicationEventPublisher);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // 清空事件列表
        eventCaptor.clear();

        mockAdminUser = new JWTModel();
        mockAdminUser.setUserId(IamTestData.ADMIN_ID);
        mockAdminUser.setUsername(IamTestData.ADMIN_USERNAME);
        mockAdminUser.setEmail(IamTestData.ADMIN_EMAIL);
        mockAdminUser.setRoles(Collections.singletonList("ADMIN"));
        mockAdminUser.setTenantId(IamTestData.TENANT_ID);

        // 載入合約並進行全域變數替換，確保測試代碼與合約文件同步
        contractSpec = loadContractSpec("iam");
        contractSpec = contractSpec.replace("{currentUserTenantId}", IamTestData.TENANT_ID);
        contractSpec = contractSpec.replace("{currentUserId}", IamTestData.ADMIN_ID);
        contractSpec = contractSpec.replace("{adminId}", IamTestData.ADMIN_ID);
        contractSpec = contractSpec.replace("{userId}", IamTestData.TEST_USER_ID);
        contractSpec = contractSpec.replace("{roleId}", IamTestData.ADMIN_ROLE_ID);
        contractSpec = contractSpec.replace("user-uuid-001", IamTestData.TEST_USER_ID);
        contractSpec = contractSpec.replace("role-uuid-001", IamTestData.ADMIN_ROLE_ID);

        // 批次處理相關的替代
        contractSpec = contractSpec.replace("user-uuid-1", IamTestData.BATCH_USER_1);
        contractSpec = contractSpec.replace("user-uuid-2", IamTestData.BATCH_USER_2);
        contractSpec = contractSpec.replace("user-uuid-3", IamTestData.TEST_USER_ID);

        contractSpec = contractSpec.replace("role-uuid-1", IamTestData.ADMIN_ROLE_ID);
        contractSpec = contractSpec.replace("role-uuid-2", IamTestData.EMPLOYEE_ROLE_ID);

        mockSecurityContext(mockAdminUser);
    }

    private void mockSecurityContext(JWTModel user) {
        List<String> auths = new ArrayList<>(user.getRoles());
        auths.add("authenticated");
        auths.add("user:read");
        auths.add("user:create");
        auths.add("user:update");
        auths.add("user:write");
        auths.add("user:activate");
        auths.add("user:deactivate");
        auths.add("user:assign-role");
        auths.add("role:read");
        auths.add("role:create");
        auths.add("role:update");
        auths.add("role:write");
        auths.add("role:delete");
        auths.add("role:assign-permission");
        auths.add("role:update-permissions");

        var authorities = AuthorityUtils.createAuthorityList(auths.toArray(new String[0]));
        var auth = new UsernamePasswordAuthenticationToken(user, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ==================== Query 測試 ====================

    @Test
    void getUserList_IAM_QRY_001() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_001");

        var result = mockMvc.perform(get("/api/v1/users?status=ACTIVE"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);

        // 驗證 Query 合約
        verifyQueryContract(null, responseJson, contract);
    }

    @Test
    void getRoleList_IAM_QRY_101() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_101");

        var result = mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        verifyQueryContract(null, responseJson, contract);
    }

    // ==================== Command 測試 ====================

    @Test
    void createUser_IAM_CMD_001() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_001");

        Map<String, Object> request = new HashMap<>();
        request.put("username", "jane.doe@company.com");
        request.put("email", "jane.doe@company.com");
        request.put("password", "Password@123");
        request.put("displayName", "Jane Doe");
        request.put("employeeId", "550e8400-e29b-41d4-a716-446655440001");
        request.put("roleIds", List.of("00000000-0000-0000-0000-000000000007")); // 使用合約中指定的角色
        request.put("sendWelcomeEmail", true);

        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users", "user_roles");

        var result = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        System.out.println("Response Status: " + result.getResponse().getStatus());
        System.out.println("Response Body: " + result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() != 201) {
            throw new AssertionError("Expected 201 but was " + result.getResponse().getStatus() +
                    "\nResponse: " + result.getResponse().getContentAsString());
        }

        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users", "user_roles");

        // 將 DomainEvent 轉換為 Map 格式供合約驗證使用
        System.out.println("Event count: " + eventCaptor.getEventCount());
        List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }

    @Test
    void updateUser_IAM_CMD_002() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_002");

        String userId = IamTestData.TEST_USER_ID;
        Map<String, Object> request = new HashMap<>();
        request.put("email", "updated.email@company.com");
        request.put("displayName", "Updated User");

        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users");

        mockMvc.perform(put("/api/v1/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users");

        List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }

    @Test
    void deactivateUser_IAM_CMD_003() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_003");

        String userId = IamTestData.TEST_USER_ID;
        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users");

        mockMvc.perform(put("/api/v1/users/" + userId + "/deactivate"))
                .andExpect(status().isNoContent());

        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users");

        List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }

    @Test
    void activateUser_IAM_CMD_004() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_004");

        String userId = IamTestData.INACTIVE_USER_ID;
        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users");

        mockMvc.perform(put("/api/v1/users/" + userId + "/activate"))
                .andExpect(status().isNoContent());

        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users");

        List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }

    @Test
    void assignUserRoles_IAM_CMD_005() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_005");

        String userId = IamTestData.TEST_USER_ID;
        Map<String, Object> request = new HashMap<>();
        request.put("roleIds", List.of(IamTestData.ADMIN_ROLE_ID, IamTestData.EMPLOYEE_ROLE_ID));

        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users", "user_roles");

        mockMvc.perform(put("/api/v1/users/" + userId + "/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users", "user_roles");

        List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }

    @Test
    void batchDeactivateUsers_IAM_CMD_006() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_006");

        Map<String, Object> request = new HashMap<>();
        request.put("userIds", List.of(IamTestData.BATCH_USER_1, IamTestData.BATCH_USER_2, IamTestData.TEST_USER_ID));

        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users");

        mockMvc.perform(put("/api/v1/users/batch-deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users");

        List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }

    // ==================== User Query 測試 ====================

    @Test
    void getUserDetail_IAM_QRY_002() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_002");

        String userId = IamTestData.TEST_USER_ID;

        var result = mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        verifyQueryContract(null, responseJson, contract);
    }

    @Test
    void getLockedUsers_IAM_QRY_004() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_004");

        var result = mockMvc.perform(get("/api/v1/users?status=LOCKED"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        verifyQueryContract(null, responseJson, contract);
    }

    @Test
    void getUsersByTenant_IAM_QRY_005() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_005");

        var result = mockMvc.perform(get("/api/v1/users?tenantId=" + IamTestData.TENANT_ID))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        verifyQueryContract(null, responseJson, contract);
    }

    // ==================== Role Command 測試 ====================

    @Test
    void createRole_IAM_CMD_101() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_101");

        Map<String, Object> request = new HashMap<>();
        request.put("roleCode", "PROJECT_MANAGER");
        request.put("roleName", "專案經理");
        request.put("description", "負責專案管理與團隊協調");
        request.put("permissionIds", List.of("perm-0001", "perm-0002")); // 使用測試資料中存在的權限

        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("roles", "role_permissions");

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("roles", "role_permissions");

        List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }

    @Test
    void updateRole_IAM_CMD_102() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_102");

        String roleId = IamTestData.MANAGER_ROLE_ID;
        Map<String, Object> request = new HashMap<>();
        request.put("roleName", "專案經理（更新）");
        request.put("description", "專案經理角色");

        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("roles");

        mockMvc.perform(put("/api/v1/roles/" + roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("roles");

        List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }

    @Test
    void deleteRole_IAM_CMD_103() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_103");

        String roleId = IamTestData.EMPLOYEE_ROLE_ID;
        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("roles");

        mockMvc.perform(delete("/api/v1/roles/" + roleId))
                .andExpect(status().isNoContent());

        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("roles");

        List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }

    @Test
    void updateRolePermissions_IAM_CMD_104() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_104");

        String roleId = IamTestData.ADMIN_ROLE_ID;
        Map<String, Object> request = new HashMap<>();
        request.put("permissionIds",
                List.of(IamTestData.PERM_USER_CREATE, IamTestData.PERM_USER_READ, IamTestData.PERM_USER_WRITE));

        // 清除現有權限，確保測試 INSERT 行為
        jdbcTemplate.update("DELETE FROM role_permissions WHERE role_id = ?", roleId);

        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("role_permissions");

        mockMvc.perform(put("/api/v1/roles/" + roleId + "/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("role_permissions");

        List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }

    // ==================== Role Query 測試 ====================

    @Test
    void getRoleByName_IAM_QRY_102() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_102");

        var result = mockMvc.perform(get("/api/v1/roles?name=管理員"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        verifyQueryContract(null, responseJson, contract);
    }

    @Test
    void getSystemRoles_IAM_QRY_103() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_103");

        var result = mockMvc.perform(get("/api/v1/roles?isSystemRole=true"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        verifyQueryContract(null, responseJson, contract);
    }

    @Test
    void getCustomRoles_IAM_QRY_104() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_104");

        var result = mockMvc.perform(get("/api/v1/roles?isSystemRole=false"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        verifyQueryContract(null, responseJson, contract);
    }

    @Test
    void getRoleDetail_IAM_QRY_105() throws Exception {
        ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_105");

        String roleId = IamTestData.ADMIN_ROLE_ID;

        var result = mockMvc.perform(get("/api/v1/roles/" + roleId))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        verifyQueryContract(null, responseJson, contract);
    }

    /**
     * 將 DomainEvent 列表轉換為 Map 格式供合約驗證使用
     */
    private List<Map<String, Object>> convertDomainEventsToMaps(List<DomainEvent> events) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (DomainEvent event : events) {
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("eventType", event.getEventType());

            // 將事件的具體欄位放入 payload
            Map<String, Object> payload = new HashMap<>();

            // 根據事件類型提取具體欄位
            if (event instanceof com.company.hrms.iam.domain.event.UserCreatedEvent) {
                com.company.hrms.iam.domain.event.UserCreatedEvent userEvent = (com.company.hrms.iam.domain.event.UserCreatedEvent) event;
                payload.put("userId", userEvent.getUserId());
                payload.put("username", userEvent.getUsername());
                payload.put("email", userEvent.getEmail());
                payload.put("displayName", userEvent.getDisplayName());
                payload.put("employeeId", userEvent.getEmployeeId());
            } else if (event instanceof com.company.hrms.iam.domain.event.UserUpdatedEvent) {
                com.company.hrms.iam.domain.event.UserUpdatedEvent userEvent = (com.company.hrms.iam.domain.event.UserUpdatedEvent) event;
                payload.put("userId", userEvent.getUserId());
                payload.put("username", userEvent.getUsername());
                payload.put("email", userEvent.getEmail());
                payload.put("displayName", userEvent.getDisplayName());
                payload.put("employeeId", userEvent.getEmployeeId());
            } else if (event instanceof com.company.hrms.iam.domain.event.UsersBatchDeactivatedEvent) {
                com.company.hrms.iam.domain.event.UsersBatchDeactivatedEvent batchEvent = (com.company.hrms.iam.domain.event.UsersBatchDeactivatedEvent) event;
                payload.put("userIds", batchEvent.getUserIds());
                payload.put("count", batchEvent.getCount());
            } else if (event instanceof com.company.hrms.iam.domain.event.UserRolesAssignedEvent) {
                com.company.hrms.iam.domain.event.UserRolesAssignedEvent roleEvent = (com.company.hrms.iam.domain.event.UserRolesAssignedEvent) event;
                payload.put("userId", roleEvent.getUserId());
                payload.put("roleIds", roleEvent.getRoleIds());
            } else if (event instanceof com.company.hrms.iam.domain.event.RolePermissionsUpdatedEvent) {
                com.company.hrms.iam.domain.event.RolePermissionsUpdatedEvent permEvent = (com.company.hrms.iam.domain.event.RolePermissionsUpdatedEvent) event;
                payload.put("roleId", permEvent.getRoleId());
                payload.put("permissionIds", permEvent.getPermissionIds());
            } else if (event instanceof com.company.hrms.iam.domain.event.RoleCreatedEvent) {
                com.company.hrms.iam.domain.event.RoleCreatedEvent roleEvent = (com.company.hrms.iam.domain.event.RoleCreatedEvent) event;
                payload.put("roleId", roleEvent.getRoleId());
                payload.put("roleCode", roleEvent.getRoleCode());
                payload.put("roleName", roleEvent.getRoleName());
            } else if (event.getEventType().equals("UserDeactivatedEvent") ||
                    event.getEventType().equals("UserActivatedEvent") ||
                    event.getEventType().equals("RoleUpdatedEvent") ||
                    event.getEventType().equals("RoleDeletedEvent")) {
                // 對於只需要 aggregateId 的簡單事件，使用通用處理
                payload.put("userId", event.getAggregateId());
                payload.put("roleId", event.getAggregateId());
            }
            eventMap.put("payload", payload);
            result.add(eventMap);
        }
        return result;
    }
}
