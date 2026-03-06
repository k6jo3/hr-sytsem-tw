package com.company.hrms.perf.simulation;

import com.company.hrms.perf.config.HrmsSimulationConfig;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * HR01 IAM 登入與 Token Refresh 效能測試
 *
 * <p>場景：
 * <ul>
 *   <li>基準測試：10 users/s × 30s 持續登入</li>
 *   <li>壓力測試：ramp 到 50 users/s</li>
 * </ul>
 */
public class AuthSimulation extends Simulation {

    // HTTP Protocol
    private final HttpProtocolBuilder httpProtocol = HrmsSimulationConfig
            .httpProtocol(HrmsSimulationConfig.IAM_BASE_URL);

    // 登入場景
    private final ScenarioBuilder loginScenario = scenario("登入場景")
            .feed(csv("data/users.csv").circular())
            .exec(
                    http("登入")
                            .post(HrmsSimulationConfig.LOGIN_PATH)
                            .body(StringBody("""
                                    {
                                        "username": "#{username}",
                                        "password": "#{password}"
                                    }
                                    """))
                            .check(status().is(200))
                            .check(jsonPath("$.token").saveAs("authToken"))
            )
            .pause(1, 3)
            .exec(
                    http("Token Refresh")
                            .post(HrmsSimulationConfig.TOKEN_REFRESH_PATH)
                            .header("Authorization", "Bearer #{authToken}")
                            .check(status().is(200))
            );

    {
        setUp(
                // 基準測試：穩定 10 users/s 持續 30 秒
                loginScenario.injectOpen(
                        constantUsersPerSec(10).during(30)
                ),
                // 壓力測試：從 1 ramp 到 50 users/s 持續 60 秒
                loginScenario.injectOpen(
                        nothingFor(35),
                        rampUsersPerSec(1).to(50).during(60)
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile3().lt(3000),
                        global().successfulRequests().percent().gt(95.0)
                );
    }
}
