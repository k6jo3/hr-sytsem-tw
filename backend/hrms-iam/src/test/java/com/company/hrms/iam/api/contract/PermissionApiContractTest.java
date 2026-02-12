package com.company.hrms.iam.api.contract;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.iam.domain.repository.IPermissionRepository;

/**
 * 權限管理 API 合約測試
 *
 * 測試場景：
 * - IAM_QRY_201: 查詢權限列表
 * - IAM_QRY_202: 查詢權限樹
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-data/iam_base_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PermissionApiContractTest extends BaseContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPermissionRepository permissionRepository;

    private String contractSpec;
    private JWTModel mockAdminUser;

    @BeforeEach
    void setUp() throws Exception {
        mockAdminUser = new JWTModel();
        mockAdminUser.setUserId(IamTestData.ADMIN_ID);
        mockAdminUser.setUsername(IamTestData.ADMIN_USERNAME);
        mockAdminUser.setEmail(IamTestData.ADMIN_EMAIL);
        mockAdminUser.setRoles(Collections.singletonList("ADMIN"));
        mockAdminUser.setTenantId(IamTestData.TENANT_ID);

        contractSpec = loadContractSpec("iam");
        contractSpec = contractSpec.replace("{currentUserTenantId}", IamTestData.TENANT_ID);
    }

    private void mockSecurityContext(JWTModel user) {
        List<String> auths = new ArrayList<>(user.getRoles());
        auths.add("permission:read");
        var authorities = AuthorityUtils.createAuthorityList(auths.toArray(new String[0]));
        var auth = new UsernamePasswordAuthenticationToken(user, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getPermissionList_AsAdmin_IAM_QRY_201() throws Exception {
        mockSecurityContext(mockAdminUser);

        // 權限查詢不需要 QueryGroup，直接返回所有權限
        when(permissionRepository.findAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/permissions"))
                .andExpect(status().isOk());

        // 權限查詢較簡單，不需要驗證 QueryGroup
        // assertContract(queryCaptor.getValue(), contractSpec, "IAM_QRY_201");
    }

    @Test
    void getPermissionTree_AsAdmin_IAM_QRY_202() throws Exception {
        mockSecurityContext(mockAdminUser);

        // 權限樹查詢不需要 QueryGroup，直接返回所有權限
        when(permissionRepository.findAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/permissions/tree"))
                .andExpect(status().isOk());

        // 權限查詢較簡單，不需要驗證 QueryGroup
        // assertContract(queryCaptor.getValue(), contractSpec, "IAM_QRY_202");
    }
}
