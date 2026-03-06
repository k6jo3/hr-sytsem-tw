package com.company.hrms.perf.simulation;

import com.company.hrms.perf.config.HrmsSimulationConfig;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * HR02 員工查詢效能測試
 *
 * <p>場景：
 * <ul>
 *   <li>分頁查詢全部員工</li>
 *   <li>按部門篩選查詢</li>
 *   <li>員工詳情查詢</li>
 * </ul>
 */
public class EmployeeQuerySimulation extends Simulation {

    // 先登入取得 Token，再執行查詢
    private final HttpProtocolBuilder httpProtocol = HrmsSimulationConfig
            .httpProtocol(HrmsSimulationConfig.IAM_BASE_URL);

    // 登入取得 Token
    private final ScenarioBuilder queryScenario = scenario("員工查詢場景")
            .exec(
                    http("登入取得 Token")
                            .post(HrmsSimulationConfig.LOGIN_PATH)
                            .body(StringBody("""
                                    {
                                        "username": "admin",
                                        "password": "Admin@123"
                                    }
                                    """))
                            .check(status().is(200))
                            .check(jsonPath("$.token").saveAs("authToken"))
            )
            .pause(1)
            // 分頁查詢
            .exec(
                    http("員工列表 - 第 1 頁")
                            .get(HrmsSimulationConfig.EMPLOYEES_PATH + "?page=0&size=10")
                            .header("Authorization", "Bearer #{authToken}")
                            .check(status().is(200))
            )
            .pause(1, 2)
            .exec(
                    http("員工列表 - 第 2 頁")
                            .get(HrmsSimulationConfig.EMPLOYEES_PATH + "?page=1&size=10")
                            .header("Authorization", "Bearer #{authToken}")
                            .check(status().is(200))
            )
            .pause(1, 2)
            // 部門篩選
            .exec(
                    http("按部門篩選")
                            .get(HrmsSimulationConfig.EMPLOYEES_PATH + "?departmentId=dept-001&page=0&size=20")
                            .header("Authorization", "Bearer #{authToken}")
                            .check(status().is(200))
            )
            .pause(1, 2)
            // 組織架構
            .exec(
                    http("部門列表")
                            .get(HrmsSimulationConfig.DEPARTMENTS_PATH + "?page=0&size=50")
                            .header("Authorization", "Bearer #{authToken}")
                            .check(status().is(200))
            );

    {
        setUp(
                queryScenario.injectOpen(
                        rampUsersPerSec(1).to(20).during(30),
                        constantUsersPerSec(20).during(60)
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile3().lt(2000),
                        global().successfulRequests().percent().gt(95.0)
                );
    }
}
