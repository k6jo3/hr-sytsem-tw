package com.company.hrms.iam.api.contract;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.common.test.contract.ContractSpec;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 個人資料管理 API 合約測試
 *
 * 測試場景：
 * - IAM_QRY_301: 查詢個人資料
 * - IAM_CMD_301: 更新個人資料
 * - IAM_CMD_302: 修改密碼
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-data/iam_base_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional(propagation = Propagation.NEVER) // 不參與 Service 事務，確保能捕獲已提交的資料變更

public class ProfileApiContractTest extends BaseContractTest {

    @Autowired
    private MockMvc mockMvc;

    // 移除 @MockBean，使用真實的 Repository 進行完整流程測試
    // @MockBean
    // private IUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String contractSpec;
    private JWTModel mockAuthenticatedUser;

    @BeforeEach
    void setUp() throws Exception {
        mockAuthenticatedUser = new JWTModel();
        mockAuthenticatedUser.setUserId(IamTestData.JOHN_DOE_ID);
        mockAuthenticatedUser.setUsername(IamTestData.JOHN_DOE_USERNAME);
        mockAuthenticatedUser.setEmail(IamTestData.JOHN_DOE_EMAIL);
        mockAuthenticatedUser.setRoles(Collections.singletonList("EMPLOYEE"));
        mockAuthenticatedUser.setTenantId(IamTestData.TENANT_ID);

        contractSpec = loadContractSpec("iam");
        // 替換合約中的佔位符
        contractSpec = contractSpec.replace("{currentUserId}", IamTestData.JOHN_DOE_ID);
        contractSpec = contractSpec.replace("{currentUserTenantId}", IamTestData.TENANT_ID);
    }

    private void mockSecurityContext(JWTModel user) {
        List<String> auths = new ArrayList<>(user.getRoles());
        auths.add("authenticated");
        auths.add("profile:read"); // Profile 查詢權限
        auths.add("profile:write"); // Profile 修改權限
        var authorities = AuthorityUtils.createAuthorityList(auths.toArray(new String[0]));
        var auth = new UsernamePasswordAuthenticationToken(user, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Nested
    class ProfileQueryApiContractTests {

        @Test
        void getProfile_AsAuthenticatedUser_IAM_QRY_301() throws Exception {
            // 載入合約（使用已替換變數的 contractSpec）
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_301");

            mockSecurityContext(mockAuthenticatedUser);

            // 執行 API 並捕獲回應
            var result = mockMvc.perform(get("/api/v1/profile"))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);

            // 驗證合約
            verifyQueryContract(null, responseJson, contract);
        }
    }

    @Nested
    class ProfileCommandApiContractTests {

        @Test
        void updateProfile_AsAuthenticatedUser_IAM_CMD_301() throws Exception {
            // 載入合約（使用已替換變數的 contractSpec）
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_301");

            mockSecurityContext(mockAuthenticatedUser);

            Map<String, Object> request = new HashMap<>();
            request.put("displayName", "John Doe Updated");
            request.put("email", "john.updated@company.com");

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users");

            // 執行 API
            mockMvc.perform(put("/api/v1/profile")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users");

            // 驗證合約
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }

        @Test
        void changePassword_AsAuthenticatedUser_IAM_CMD_302() throws Exception {
            // 載入合約（使用已替換變數的 contractSpec）
            ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_CMD_302");

            mockSecurityContext(mockAuthenticatedUser);

            Map<String, Object> request = new HashMap<>();
            request.put("currentPassword", "password123"); // API 要求的欄位名稱
            request.put("newPassword", "NewSecure@Pass456"); // 包含特殊字元
            request.put("confirmPassword", "NewSecure@Pass456"); // 確認密碼

            // 擷取快照
            Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("users");

            // 執行 API（期望 204 No Content，符合 REST API 規範）
            mockMvc.perform(put("/api/v1/profile/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("users");

            // 驗證合約
            List<Map<String, Object>> capturedEvents = new ArrayList<>();
            verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
        }
    }
}
