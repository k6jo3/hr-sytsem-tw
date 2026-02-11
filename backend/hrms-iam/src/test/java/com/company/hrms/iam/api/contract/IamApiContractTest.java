package com.company.hrms.iam.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.common.test.contract.ContractSpec;
import com.company.hrms.iam.domain.repository.IPermissionRepository;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-data/iam_base_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class IamApiContractTest extends BaseContractTest {

    private String contractSpec;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserRepository userRepository;

    @MockBean
    private IRoleRepository roleRepository;

    @MockBean
    private IPermissionRepository permissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private JWTModel mockAdminUser;
    private JWTModel mockSuperAdminUser;

    @BeforeEach
    void setUp() throws Exception {
        mockAdminUser = new JWTModel();
        mockAdminUser.setUserId("user-admin-id");
        mockAdminUser.setUsername("admin");
        mockAdminUser.setEmail("admin@company.com");
        mockAdminUser.setRoles(Collections.singletonList("ADMIN"));
        mockAdminUser.setTenantId("T001");

        mockSuperAdminUser = new JWTModel();
        mockSuperAdminUser.setUserId("user-super-id");
        mockSuperAdminUser.setUsername("super_admin");
        mockSuperAdminUser.setRoles(Collections.singletonList("SUPER_ADMIN"));
        mockSuperAdminUser.setTenantId("T001");

        // 使用 BaseContractTest 的 loadContractSpec 方法
        contractSpec = loadContractSpec("iam");
        // 替換合約中的佔位符
        contractSpec = contractSpec.replace("{currentUserTenantId}", "T001");
        contractSpec = contractSpec.replace("{currentUserId}", "user-admin-id");
    }

    private void mockSecurityContext(JWTModel user) {
        List<String> auths = new ArrayList<>(user.getRoles());
        // 加入所有必要的權限
        auths.add("authenticated"); // 基本認證權限
        // User 相關權限
        auths.add("user:read");
        auths.add("user:create");
        auths.add("user:update");
        auths.add("user:activate");
        auths.add("user:deactivate");
        auths.add("user:assign-role");
        // Role 相關權限
        auths.add("role:read");
        auths.add("role:create");
        auths.add("role:update");
        auths.add("role:delete");
        auths.add("role:assign-permission");
        var authorities = AuthorityUtils.createAuthorityList(auths.toArray(new String[0]));
        var auth = new UsernamePasswordAuthenticationToken(user, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Nested
    class UserQueryApiContractTests {
        @Test
        void searchActiveUsers_AsAdmin() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_001");

            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(userRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // 執行 API 並捕獲回應
            var result = mockMvc.perform(get("/api/v1/users?status=ACTIVE"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString();

            // 驗證合約
            verifyQueryContract(queryCaptor.getValue(), responseJson, contract);
        }

        @Test
        void searchLockedUsers_AsAdmin() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_004");

            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(userRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // 執行 API 並捕獲回應
            var result = mockMvc.perform(get("/api/v1/users?status=LOCKED"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString();

            // 驗證合約
            verifyQueryContract(queryCaptor.getValue(), responseJson, contract);
        }

        @Test
        void searchByTenant_AsSuperAdmin() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_005");

            mockSecurityContext(mockSuperAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(userRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // 執行 API 並捕獲回應
            var result = mockMvc.perform(get("/api/v1/users?tenantId=T001"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString();

            // 驗證合約
            verifyQueryContract(queryCaptor.getValue(), responseJson, contract);
        }

        @Test
        void getUserDetail_AsAdmin_IAM_QRY_002() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_002");

            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(userRepository.findAll(queryCaptor.capture()))
                    .thenReturn(Collections.emptyList());

            // 執行 API 並捕獲回應
            var result = mockMvc.perform(get("/api/v1/users/user-uuid-001"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString();

            // 驗證合約
            verifyQueryContract(queryCaptor.getValue(), responseJson, contract);
        }
    }

    @Nested
    class RoleQueryApiContractTests {
        @Test
        void searchAllRoles_AsAdmin() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_101");

            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(roleRepository.findAll(queryCaptor.capture())).thenReturn(Collections.emptyList());

            // 執行 API 並捕獲回應
            var result = mockMvc.perform(get("/api/v1/roles"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString();

            // 驗證合約
            verifyQueryContract(queryCaptor.getValue(), responseJson, contract);
        }

        @Test
        void searchRolesByName_AsAdmin() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_102");

            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(roleRepository.findAll(queryCaptor.capture())).thenReturn(Collections.emptyList());

            // 執行 API 並捕獲回應
            var result = mockMvc.perform(get("/api/v1/roles?name=管理"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString();

            // 驗證合約
            verifyQueryContract(queryCaptor.getValue(), responseJson, contract);
        }

        @Test
        void searchSystemRoles_AsAdmin() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_103");

            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(roleRepository.findAll(queryCaptor.capture())).thenReturn(Collections.emptyList());

            // 執行 API 並捕獲回應
            var result = mockMvc.perform(get("/api/v1/roles?isSystemRole=true"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString();

            // 驗證合約
            verifyQueryContract(queryCaptor.getValue(), responseJson, contract);
        }

        @Test
        void searchCustomRoles_AsAdmin() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_104");

            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(roleRepository.findAll(queryCaptor.capture())).thenReturn(Collections.emptyList());

            // 執行 API 並捕獲回應
            var result = mockMvc.perform(get("/api/v1/roles?isSystemRole=false"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString();

            // 驗證合約
            verifyQueryContract(queryCaptor.getValue(), responseJson, contract);
        }

        @Test
        void getRoleDetail_AsAdmin_IAM_QRY_105() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_105");

            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(roleRepository.findAll(queryCaptor.capture()))
                    .thenReturn(Collections.emptyList());

            // 執行 API 並捕獲回應
            var result = mockMvc.perform(get("/api/v1/roles/role-uuid-001"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString();

            // 驗證合約
            verifyQueryContract(queryCaptor.getValue(), responseJson, contract);
        }
    }

    @Nested
    class UserCommandApiContractTests {

        @Test
        void createUser_AsAdmin_IAM_CMD_001() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_001");

            // 準備測試資料
            mockSecurityContext(mockAdminUser);

            Map<String, Object> request = new HashMap<>();
            request.put("username", "jane.doe@company.com");
            request.put("email", "jane.doe@company.com");
            request.put("employeeId", "550e8400-e29b-41d4-a716-446655440001");
            request.put("roleIds", List.of("00000000-0000-0000-0000-000000000007"));
            request.put("sendWelcomeEmail", true);

            // 擷取執行前快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users", "user_roles");

            // 執行 API
            mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // 擷取執行後快照
            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users", "user_roles");

            // 驗證完整合約（資料異動 + 領域事件）
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            // TODO: 從事件監聽器取得捕獲的事件

            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }

        @Test
        void updateUser_AsAdmin_IAM_CMD_002() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_002");

            mockSecurityContext(mockAdminUser);

            String userId = "user-001";
            Map<String, Object> request = new HashMap<>();
            request.put("email", "jane.new@company.com");

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users");

            // 執行 API
            mockMvc.perform(put("/api/v1/users/" + userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users");

            // 驗證合約
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }

        @Test
        void deactivateUser_AsAdmin_IAM_CMD_003() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_003");

            mockSecurityContext(mockAdminUser);

            String userId = "user-001";

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users");

            // 執行 API
            mockMvc.perform(put("/api/v1/users/" + userId + "/deactivate"))
                    .andExpect(status().isNoContent());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users");

            // 驗證合約
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }

        @Test
        void activateUser_AsAdmin_IAM_CMD_004() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_004");

            mockSecurityContext(mockAdminUser);

            String userId = "user-001";

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users");

            // 執行 API
            mockMvc.perform(put("/api/v1/users/" + userId + "/activate"))
                    .andExpect(status().isNoContent());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users");

            // 驗證合約
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }

        @Test
        void assignUserRoles_AsAdmin_IAM_CMD_005() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_005");

            mockSecurityContext(mockAdminUser);

            String userId = "user-001";
            Map<String, Object> request = new HashMap<>();
            request.put("roleIds", List.of("role-uuid-1", "role-uuid-2"));

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("user_roles");

            // 執行 API
            mockMvc.perform(put("/api/v1/users/" + userId + "/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("user_roles");

            // 驗證合約
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }

        @Test
        void batchDeactivateUsers_AsAdmin_IAM_CMD_006() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_006");

            mockSecurityContext(mockAdminUser);

            Map<String, Object> request = new HashMap<>();
            request.put("userIds", List.of("user-uuid-1", "user-uuid-2", "user-uuid-3"));

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users");

            // 執行 API
            mockMvc.perform(put("/api/v1/users/batch-deactivate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users");

            // 驗證合約
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }
    }

    @Nested
    class RoleCommandApiContractTests {

        @Test
        void createRole_AsAdmin_IAM_CMD_101() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_101");

            mockSecurityContext(mockAdminUser);

            Map<String, Object> request = new HashMap<>();
            request.put("roleCode", "PROJECT_MANAGER");
            request.put("roleName", "專案經理");
            request.put("description", "負責專案管理與團隊協調");
            request.put("permissionIds", List.of("perm-uuid-1", "perm-uuid-2"));

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("roles", "role_permissions");

            // 執行 API
            mockMvc.perform(post("/api/v1/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("roles", "role_permissions");

            // 驗證合約
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }

        @Test
        void updateRole_AsAdmin_IAM_CMD_102() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_102");

            mockSecurityContext(mockAdminUser);

            String roleId = "role-001";
            Map<String, Object> request = new HashMap<>();
            request.put("roleName", "專案經理（高級）");
            request.put("description", "負責大型專案管理與跨部門協調");

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("roles");

            // 執行 API
            mockMvc.perform(put("/api/v1/roles/" + roleId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("roles");

            // 驗證合約
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }

        @Test
        void deleteRole_AsAdmin_IAM_CMD_103() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_103");

            mockSecurityContext(mockAdminUser);

            String roleId = "role-001";

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("roles");

            // 執行 API
            mockMvc.perform(delete("/api/v1/roles/" + roleId))
                    .andExpect(status().isNoContent());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("roles");

            // 驗證合約（軟刪除）
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }

        @Test
        void assignPermissions_AsAdmin_IAM_CMD_104() throws Exception {
            // 載入合約
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_104");

            mockSecurityContext(mockAdminUser);

            String roleId = "role-001";
            Map<String, Object> request = new HashMap<>();
            request.put("permissionIds", List.of("perm-uuid-3", "perm-uuid-4", "perm-uuid-5"));

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("role_permissions");

            // 執行 API
            mockMvc.perform(put("/api/v1/roles/" + roleId + "/permissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("role_permissions");

            // 驗證合約
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }
    }
}
