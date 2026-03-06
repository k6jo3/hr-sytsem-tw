package com.company.hrms.perf.simulation;

import com.company.hrms.perf.config.HrmsSimulationConfig;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * 混合負載效能測試
 *
 * <p>模擬真實使用場景的混合負載：
 * <ul>
 *   <li>60% 查詢操作（員工列表、部門查詢）</li>
 *   <li>30% 輕量寫入（打卡）</li>
 *   <li>10% 重操作（報表查詢）</li>
 * </ul>
 */
public class MixedWorkloadSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = HrmsSimulationConfig
            .httpProtocol(HrmsSimulationConfig.IAM_BASE_URL);

    // 共用：登入取得 Token
    private final ChainBuilder login = exec(
            http("登入")
                    .post(HrmsSimulationConfig.LOGIN_PATH)
                    .body(StringBody("""
                            {
                                "username": "admin",
                                "password": "Admin@123"
                            }
                            """))
                    .check(status().is(200))
                    .check(jsonPath("$.token").saveAs("authToken"))
    ).pause(1);

    // 查詢操作（60%）
    private final ScenarioBuilder queryScenario = scenario("查詢操作")
            .exec(login)
            .repeat(5).on(
                    exec(
                            http("員工列表查詢")
                                    .get(HrmsSimulationConfig.EMPLOYEES_PATH + "?page=0&size=10")
                                    .header("Authorization", "Bearer #{authToken}")
                                    .check(status().is(200))
                    ).pause(2, 5)
            );

    // 輕量寫入（30%）— 打卡
    private final ScenarioBuilder writeScenario = scenario("輕量寫入")
            .exec(login)
            .repeat(3).on(
                    exec(
                            http("打卡")
                                    .post(HrmsSimulationConfig.CLOCK_IN_PATH)
                                    .header("Authorization", "Bearer #{authToken}")
                                    .body(StringBody("""
                                            {
                                                "clockType": "CLOCK_IN",
                                                "location": "辦公室"
                                            }
                                            """))
                                    .check(status().in(200, 201, 400))
                    ).pause(3, 8)
            );

    // 重操作（10%）— 報表
    private final ScenarioBuilder heavyScenario = scenario("重操作")
            .exec(login)
            .exec(
                    http("員工花名冊報表")
                            .get(HrmsSimulationConfig.REPORTING_BASE_URL
                                    + "/api/v1/reports/employee-roster?page=0&size=100")
                            .header("Authorization", "Bearer #{authToken}")
                            .check(status().in(200, 404))
            ).pause(5, 10);

    {
        setUp(
                // 60% 查詢
                queryScenario.injectOpen(
                        rampUsersPerSec(1).to(12).during(30),
                        constantUsersPerSec(12).during(120)
                ),
                // 30% 寫入
                writeScenario.injectOpen(
                        rampUsersPerSec(1).to(6).during(30),
                        constantUsersPerSec(6).during(120)
                ),
                // 10% 重操作
                heavyScenario.injectOpen(
                        rampUsersPerSec(0.5).to(2).during(30),
                        constantUsersPerSec(2).during(120)
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile3().lt(5000),
                        global().successfulRequests().percent().gt(90.0)
                );
    }
}
