package com.company.hrms.iam.api.contract;

/**
 * IAM 合約測試共用測試資料常數
 */
public class IamTestData {
    // 租戶 ID
    public static final String TENANT_ID = "T001";
    public static final String DEFAULT_TENANT_ID = "00000000-0000-0000-0000-000000000001";

    // 使用者 ID (與 iam_base_data.sql 同步)
    public static final String ADMIN_ID = "00000000-0000-0000-0000-000000000001";
    public static final String TEST_USER_ID = "00000000-0000-0000-0000-000000000111"; // test.user@company.com
    public static final String INACTIVE_USER_ID = "00000000-0000-0000-0000-000000000222";
    public static final String BATCH_USER_1 = "00000000-0000-0000-0000-000000000333";
    public static final String BATCH_USER_2 = "00000000-0000-0000-0000-000000000444";
    public static final String JOHN_DOE_ID = "00000000-0000-0000-0000-000000000999";

    // 使用者名稱與 Email
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_EMAIL = "admin@company.com";
    public static final String TEST_USER_USERNAME = "test.user@company.com";
    public static final String TEST_USER_EMAIL = "test.user@company.com";
    public static final String JOHN_DOE_USERNAME = "john.doe@company.com";
    public static final String JOHN_DOE_EMAIL = "john.doe@company.com";

    // 角色 ID (與 iam_base_data.sql 同步)
    public static final String ADMIN_ROLE_ID = "a0000000-0000-0000-0000-000000000001";
    public static final String EMPLOYEE_ROLE_ID = "a0000000-0000-0000-0000-000000000002";
    public static final String MANAGER_ROLE_ID = "a0000000-0000-0000-0000-000000000003";
    public static final String HR_ROLE_ID = "00000000-0000-0000-0000-000000000007";

    // 權限 ID
    public static final String PERM_USER_CREATE = "perm-0001";
    public static final String PERM_USER_READ = "perm-0002";
    public static final String PERM_USER_WRITE = "perm-0003";

    // 密碼相關
    public static final String DEFAULT_PASSWORD = "password123";
}
