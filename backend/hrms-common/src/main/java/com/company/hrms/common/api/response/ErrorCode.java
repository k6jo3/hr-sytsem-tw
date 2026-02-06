package com.company.hrms.common.api.response;

/**
 * 錯誤碼常數定義
 * 提供統一的錯誤碼規範
 *
 * <p>
 * 錯誤碼格式：{服務代號}{錯誤類型}{序號}
 * <ul>
 * <li>服務代號：01=IAM, 02=ORG, 03=ATT, 等</li>
 * <li>錯誤類型：4=客戶端錯誤, 5=伺服器錯誤</li>
 * <li>序號：001~999</li>
 * </ul>
 *
 * <p>
 * 通用錯誤碼使用 00 作為服務代號
 */
public final class ErrorCode {

    private ErrorCode() {
        // 私有建構子防止實例化
    }

    // ========================================
    // 通用錯誤碼 (00)
    // ========================================

    /** 請求參數驗證失敗 */
    public static final String VALIDATION_ERROR = "004001";

    /** 資源不存在 */
    public static final String RESOURCE_NOT_FOUND = "004004";

    /** 資源已存在 */
    public static final String RESOURCE_ALREADY_EXISTS = "004009";

    /** 請求方法不允許 */
    public static final String METHOD_NOT_ALLOWED = "004005";

    /** 請求內容類型不支援 */
    public static final String UNSUPPORTED_MEDIA_TYPE = "004015";

    /** 伺服器內部錯誤 */
    public static final String INTERNAL_SERVER_ERROR = "005001";

    /** 資料庫操作失敗 */
    public static final String DATABASE_ERROR = "005002";

    /** 外部服務呼叫失敗 */
    public static final String EXTERNAL_SERVICE_ERROR = "005003";

    // ========================================
    // IAM 服務錯誤碼 (01)
    // ========================================

    /** 未授權存取 */
    public static final String IAM_UNAUTHORIZED = "014001";

    /** 存取被拒絕 */
    public static final String IAM_FORBIDDEN = "014003";

    /** 使用者不存在 */
    public static final String IAM_USER_NOT_FOUND = "014004";

    /** 帳號或密碼錯誤 */
    public static final String IAM_INVALID_CREDENTIALS = "014010";

    /** 帳號已被鎖定 */
    public static final String IAM_ACCOUNT_LOCKED = "014011";

    /** 帳號已停用 */
    public static final String IAM_ACCOUNT_DISABLED = "014012";

    /** Token 已過期 */
    public static final String IAM_TOKEN_EXPIRED = "014013";

    /** Token 無效 */
    public static final String IAM_TOKEN_INVALID = "014014";

    /** 電子郵件已存在 */
    public static final String IAM_EMAIL_EXISTS = "014020";

    /** 角色不存在 */
    public static final String IAM_ROLE_NOT_FOUND = "014030";

    /** 權限不存在 */
    public static final String IAM_PERMISSION_NOT_FOUND = "014031";

    // ========================================
    // 組織服務錯誤碼 (02)
    // ========================================

    /** 員工不存在 */
    public static final String ORG_EMPLOYEE_NOT_FOUND = "024004";

    /** 部門不存在 */
    public static final String ORG_DEPARTMENT_NOT_FOUND = "024010";

    /** 職位不存在 */
    public static final String ORG_POSITION_NOT_FOUND = "024011";

    /** 員工編號已存在 */
    public static final String ORG_EMPLOYEE_ID_EXISTS = "024020";

    // ========================================
    // 考勤服務錯誤碼 (03)
    // ========================================

    /** 打卡紀錄不存在 */
    public static final String ATT_RECORD_NOT_FOUND = "034004";

    /** 重複打卡 */
    public static final String ATT_DUPLICATE_PUNCH = "034010";

    /** 請假申請不存在 */
    public static final String ATT_LEAVE_NOT_FOUND = "034011";

    /** 假別餘額不足 */
    public static final String ATT_INSUFFICIENT_LEAVE_BALANCE = "034012";

    // ========================================
    // 薪資服務錯誤碼 (04)
    // ========================================

    /** 薪資紀錄不存在 */
    public static final String PAY_RECORD_NOT_FOUND = "044004";

    /** 薪資計算失敗 */
    public static final String PAY_CALCULATION_ERROR = "045001";

    // ========================================
    // 專案服務錯誤碼 (06)
    // ========================================

    /** 專案不存在 */
    public static final String PRJ_PROJECT_NOT_FOUND = "064004";

    /** WBS 節點不存在 */
    public static final String PRJ_WBS_NOT_FOUND = "064010";

    // ========================================
    // 工時服務錯誤碼 (07)
    // ========================================

    /** 工時紀錄不存在 */
    public static final String TMS_RECORD_NOT_FOUND = "074004";

    /** 工時超過上限 */
    public static final String TMS_HOURS_EXCEEDED = "074010";
}
