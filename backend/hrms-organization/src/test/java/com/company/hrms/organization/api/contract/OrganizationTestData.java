package com.company.hrms.organization.api.contract;

/**
 * HR02 組織員工服務合約測試共用測試資料常數
 * 與 organization_test_data.sql 同步
 */
public class OrganizationTestData {

    // ==================== 組織 ID ====================
    public static final String ORG_HEAD_OFFICE_ID = "11111111-1111-1111-1111-111111111111";
    public static final String ORG_SUBSIDIARY_A_ID = "22222222-2222-2222-2222-222222222222";

    // ==================== 部門 ID (與 organization_test_data.sql 同步) ====================
    public static final String DEPT_RD_ID = "d0000001-0001-0001-0001-000000000001";       // 研發部 (ACTIVE)
    public static final String DEPT_SALES_ID = "d0000002-0002-0002-0002-000000000002";     // 業務部 (ACTIVE)
    public static final String DEPT_FIN_ID = "d0000003-0003-0003-0003-000000000003";       // 財務部 (ACTIVE)
    public static final String DEPT_HR_ID = "d0000004-0004-0004-0004-000000000004";        // 人事部 (ACTIVE)
    public static final String DEPT_RD_FE_ID = "d0000005-0005-0005-0005-000000000005";     // 前端組 (ACTIVE, 研發部子部門)
    public static final String DEPT_RD_BE_ID = "d0000006-0006-0006-0006-000000000006";     // 後端組 (INACTIVE, 研發部子部門)

    // ==================== 員工 ID (與 organization_test_data.sql 同步) ====================
    // ACTIVE 員工 (8人)
    public static final String EMP_WANG_ID = "e0000001-0001-0001-0001-000000000001";       // 王小明, 研發部, ACTIVE
    public static final String EMP_LI_ZQ_ID = "e0000002-0002-0002-0002-000000000002";     // 李志強, 研發部, ACTIVE
    public static final String EMP_ZHANG_ML_ID = "e0000003-0003-0003-0003-000000000003";   // 張美麗, 研發部, ACTIVE
    public static final String EMP_LIU_DW_ID = "e0000004-0004-0004-0004-000000000004";     // 劉大為, 研發部, ACTIVE
    public static final String EMP_CHEN_SF_ID = "e0000005-0005-0005-0005-000000000005";     // 陳淑芬, 研發部, ACTIVE
    public static final String EMP_ZHAO_JG_ID = "e0000006-0006-0006-0006-000000000006";     // 趙建國, 業務部, ACTIVE
    public static final String EMP_SUN_AH_ID = "e0000007-0007-0007-0007-000000000007";     // 孫愛華, 財務部, ACTIVE
    public static final String EMP_HUANG_ZQ_ID = "e0000008-0008-0008-0008-000000000008";   // 黃自強, 人事部, ACTIVE

    // PROBATION 員工 (3人)
    public static final String EMP_ZHOU_JJ_ID = "e0000009-0009-0009-0009-000000000009";     // 周俊傑, 業務部, PROBATION
    public static final String EMP_LIN_RL_ID = "e0000010-0010-0010-0010-000000000010";     // 林若蘭, 財務部, PROBATION
    public static final String EMP_WU_ZM_ID = "e0000011-0011-0011-0011-000000000011";       // 吳志明, 人事部, PROBATION

    // TERMINATED 員工 (2人)
    public static final String EMP_ZHANG_LW_ID = "e0000012-0012-0012-0012-000000000012";   // 張老王, 業務部, TERMINATED
    public static final String EMP_LI_XH_ID = "e0000013-0013-0013-0013-000000000013";     // 李小紅, 財務部, TERMINATED

    // 特殊狀態員工
    public static final String EMP_ZHENG_AH_ID = "e0000014-0014-0014-0014-000000000014";   // 鄭阿豪, 人事部, UNPAID_LEAVE
    public static final String EMP_CHEN_XM_ID = "e0000015-0015-0015-0015-000000000015";     // 陳小美, 人事部, PARENTAL_LEAVE

    // ==================== 員工編號 ====================
    public static final String EMP_NUMBER_WANG = "EMP202501-001";
    public static final String EMP_NUMBER_LI_ZQ = "EMP202501-002";

    // ==================== 模擬使用者常數 ====================
    public static final String ADMIN_USER_ID = "admin-user-001";
    public static final String ADMIN_USERNAME = "admin";
    public static final String HR_USER_ID = "hr-user-001";
}
