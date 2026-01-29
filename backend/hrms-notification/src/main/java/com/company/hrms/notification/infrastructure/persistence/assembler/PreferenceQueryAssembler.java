package com.company.hrms.notification.infrastructure.persistence.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.QueryGroup;

/**
 * 通知偏好設定查詢條件組裝器
 * <p>
 * 負責將查詢條件轉換為 QueryGroup (Fluent-Query-Engine)
 * </p>
 * <p>
 * 採用宣告式查詢模式（參考 HR03 Attendance）：
 * - 使用 QueryGroup.and() 作為起點
 * - 使用流暢方法鏈 (query.eq() 等)
 * - 條件式添加過濾器
 * </p>
 * <p>
 * 注意：QueryBuilder.eq() 使用 JPA entity 欄位名稱 (camelCase)，
 * NOT database column names (snake_case)
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Component
public class PreferenceQueryAssembler {

    /**
     * 根據員工 ID 查詢偏好設定
     *
     * @param employeeId 員工 ID
     * @return QueryGroup
     */
    public QueryGroup queryByEmployeeId(String employeeId) {
        QueryGroup query = QueryGroup.and();

        query.eq("employeeId", employeeId);
        query.eq("isDeleted", false);

        return query;
    }

    /**
     * 檢查員工是否已有偏好設定
     *
     * @param employeeId 員工 ID
     * @return QueryGroup
     */
    public QueryGroup existsByEmployeeId(String employeeId) {
        QueryGroup query = QueryGroup.and();

        query.eq("employeeId", employeeId);
        query.eq("isDeleted", false);

        return query;
    }

    /**
     * 查詢啟用 Email 通知的使用者偏好設定
     *
     * @return QueryGroup
     */
    public QueryGroup queryEmailEnabledPreferences() {
        QueryGroup query = QueryGroup.and();

        query.eq("emailEnabled", true);
        query.eq("isDeleted", false);

        return query;
    }

    /**
     * 查詢啟用推播通知的使用者偏好設定
     *
     * @return QueryGroup
     */
    public QueryGroup queryPushEnabledPreferences() {
        QueryGroup query = QueryGroup.and();

        query.eq("pushEnabled", true);
        query.eq("isDeleted", false);

        return query;
    }
}
