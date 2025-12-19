package com.company.hrms.common.test.base;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.snapshot.SnapshotConfig;
import com.company.hrms.common.test.snapshot.SnapshotUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

/**
 * 測試基類 - 通用配置
 * 提供所有測試共用的配置與工具方法
 *
 * <p>繼承關係:
 * <pre>
 * BaseTest
 *   ├── BaseUnitTest
 *   │     ├── BaseDomainTest
 *   │     └── BaseServiceTest
 *   └── BaseIntegrationTest
 *         ├── BaseApiContractTest
 *         └── BaseRepositoryTest
 * </pre>
 */
public abstract class BaseTest {

    /** 測試用的模擬使用者 */
    protected JWTModel mockUser;

    /** 管理員角色的模擬使用者 */
    protected JWTModel adminUser;

    /** 一般員工角色的模擬使用者 */
    protected JWTModel employeeUser;

    /** ObjectMapper 實例 */
    protected ObjectMapper objectMapper;

    /** 快照配置 */
    protected SnapshotConfig snapshotConfig;

    @BeforeEach
    void setUpBase() {
        // 初始化模擬使用者
        mockUser = createMockUser("test-user-001", "Test User", Arrays.asList("EMPLOYEE"));
        adminUser = createMockUser("admin-001", "Admin User", Arrays.asList("ADMIN", "EMPLOYEE"));
        employeeUser = createMockUser("emp-001", "Employee User", Arrays.asList("EMPLOYEE"));

        // 初始化 ObjectMapper
        objectMapper = SnapshotUtils.getDefaultMapper();

        // 初始化快照配置
        snapshotConfig = SnapshotConfig.builder()
            .ignoreCommonDynamicFields()
            .build();
    }

    /**
     * 建立模擬使用者
     */
    protected JWTModel createMockUser(String userId, String username, List<String> roles) {
        JWTModel user = new JWTModel();
        user.setUserId(userId);
        user.setUsername(username);
        user.setRoles(roles);
        return user;
    }

    /**
     * 建立具有特定權限的模擬使用者
     */
    protected JWTModel createMockUserWithPermissions(String userId, String username,
                                                      List<String> roles, List<String> permissions) {
        JWTModel user = createMockUser(userId, username, roles);
        user.setPermissions(permissions);
        return user;
    }

    /**
     * 取得測試類別名稱（用於快照目錄）
     */
    protected String getTestClassName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 取得快照路徑
     */
    protected String snapshotPath(String snapshotName) {
        return getTestClassName() + "/" + snapshotName;
    }
}
