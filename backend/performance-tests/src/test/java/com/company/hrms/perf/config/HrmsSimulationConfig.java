package com.company.hrms.perf.config;

import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * HRMS 效能測試共用設定
 *
 * <p>提供各微服務 base URL、共用 HTTP protocol、JWT Token 取得邏輯。
 */
public final class HrmsSimulationConfig {

    private HrmsSimulationConfig() {
    }

    // 各服務 Base URL（Local Profile）
    public static final String IAM_BASE_URL = "http://localhost:8081";
    public static final String ORGANIZATION_BASE_URL = "http://localhost:8082";
    public static final String ATTENDANCE_BASE_URL = "http://localhost:8083";
    public static final String PAYROLL_BASE_URL = "http://localhost:8084";
    public static final String INSURANCE_BASE_URL = "http://localhost:8085";
    public static final String PROJECT_BASE_URL = "http://localhost:8086";
    public static final String TIMESHEET_BASE_URL = "http://localhost:8087";
    public static final String PERFORMANCE_BASE_URL = "http://localhost:8088";
    public static final String RECRUITMENT_BASE_URL = "http://localhost:8089";
    public static final String TRAINING_BASE_URL = "http://localhost:8090";
    public static final String WORKFLOW_BASE_URL = "http://localhost:8091";
    public static final String NOTIFICATION_BASE_URL = "http://localhost:8092";
    public static final String DOCUMENT_BASE_URL = "http://localhost:8093";
    public static final String REPORTING_BASE_URL = "http://localhost:8094";

    // 共用 HTTP Protocol
    public static HttpProtocolBuilder httpProtocol(String baseUrl) {
        return http.baseUrl(baseUrl)
                .acceptHeader("application/json")
                .contentTypeHeader("application/json")
                .acceptLanguageHeader("zh-TW,zh;q=0.9,en;q=0.8");
    }

    // 登入端點
    public static final String LOGIN_PATH = "/api/v1/auth/login";

    // 員工查詢端點
    public static final String EMPLOYEES_PATH = "/api/v1/employees";

    // 部門查詢端點
    public static final String DEPARTMENTS_PATH = "/api/v1/departments";

    // 考勤打卡端點
    public static final String CLOCK_IN_PATH = "/api/v1/attendance/clock-in";

    // Token Refresh 端點
    public static final String TOKEN_REFRESH_PATH = "/api/v1/auth/refresh";
}
