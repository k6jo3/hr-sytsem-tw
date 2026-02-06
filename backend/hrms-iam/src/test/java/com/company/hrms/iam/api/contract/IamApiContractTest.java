package com.company.hrms.iam.api.contract;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.iam.domain.repository.IPermissionRepository;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import com.company.hrms.iam.domain.repository.IUserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class IamApiContractTest extends BaseApiContractTest {

    private String contractSpec;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserRepository userRepository;

    @MockBean
    private IRoleRepository roleRepository;

    @MockBean
    private IPermissionRepository permissionRepository;

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

        Path projectRoot = Paths.get("").toAbsolutePath();
        Path contractPath = null;
        Path current = projectRoot;
        for (int i = 0; i < 5; i++) {
            Path candidate = current.resolve("contracts/iam_contracts_v2.md");
            if (Files.exists(candidate)) {
                contractPath = candidate;
                break;
            }
            current = current.getParent();
            if (current == null)
                break;
        }

        if (contractPath == null)
            throw new RuntimeException("Cannot find contract file");

        contractSpec = Files.readString(contractPath);
        contractSpec = contractSpec.replace("{currentUserTenantId}", "T001");
    }

    private void mockSecurityContext(JWTModel user) {
        List<String> auths = new ArrayList<>(user.getRoles());
        auths.add("user:read");
        auths.add("role:read");
        var authorities = AuthorityUtils.createAuthorityList(auths.toArray(new String[0]));
        var auth = new UsernamePasswordAuthenticationToken(user, "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Nested
    class UserQueryApiContractTests {
        @Test
        void searchActiveUsers_AsAdmin() throws Exception {
            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(userRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            mockMvc.perform(get("/api/v1/users?status=ACTIVE")).andExpect(status().isOk());
            assertContract(queryCaptor.getValue(), contractSpec, "IAM_QRY_001");
        }

        @Test
        void searchByKeyword_AsAdmin() throws Exception {
            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(userRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            mockMvc.perform(get("/api/v1/users?keyword=john")).andExpect(status().isOk());
            assertThat(queryCaptor.getValue().getAllFilters().toString()).contains("john");
        }

        @Test
        void searchByRole_AsAdmin() throws Exception {
            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(userRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            mockMvc.perform(get("/api/v1/users?roleId=R001")).andExpect(status().isOk());
            assertThat(queryCaptor.getValue().getAllFilters().toString()).contains("R001");
        }

        @Test
        void searchLockedUsers_AsAdmin() throws Exception {
            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(userRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            mockMvc.perform(get("/api/v1/users?status=LOCKED")).andExpect(status().isOk());
            assertContract(queryCaptor.getValue(), contractSpec, "IAM_QRY_004");
        }

        @Test
        void searchByTenant_AsSuperAdmin() throws Exception {
            mockSecurityContext(mockSuperAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(userRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            mockMvc.perform(get("/api/v1/users?tenantId=T001")).andExpect(status().isOk());
            assertContract(queryCaptor.getValue(), contractSpec, "IAM_QRY_005");
        }
    }

    @Nested
    class RoleQueryApiContractTests {
        @Test
        void searchAllRoles_AsAdmin() throws Exception {
            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(roleRepository.findAll(queryCaptor.capture())).thenReturn(Collections.emptyList());
            mockMvc.perform(get("/api/v1/roles")).andExpect(status().isOk());
            assertContract(queryCaptor.getValue(), contractSpec, "IAM_QRY_101");
        }

        @Test
        void searchRolesByName_AsAdmin() throws Exception {
            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(roleRepository.findAll(queryCaptor.capture())).thenReturn(Collections.emptyList());
            mockMvc.perform(get("/api/v1/roles?name=管理")).andExpect(status().isOk());
            assertContract(queryCaptor.getValue(), contractSpec, "IAM_QRY_102");
        }

        @Test
        void searchSystemRoles_AsAdmin() throws Exception {
            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(roleRepository.findAll(queryCaptor.capture())).thenReturn(Collections.emptyList());
            mockMvc.perform(get("/api/v1/roles?isSystemRole=true")).andExpect(status().isOk());
            assertContract(queryCaptor.getValue(), contractSpec, "IAM_QRY_103");
        }

        @Test
        void searchCustomRoles_AsAdmin() throws Exception {
            mockSecurityContext(mockAdminUser);
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(roleRepository.findAll(queryCaptor.capture())).thenReturn(Collections.emptyList());
            mockMvc.perform(get("/api/v1/roles?isSystemRole=false")).andExpect(status().isOk());
            assertContract(queryCaptor.getValue(), contractSpec, "IAM_QRY_104");
        }
    }
}
