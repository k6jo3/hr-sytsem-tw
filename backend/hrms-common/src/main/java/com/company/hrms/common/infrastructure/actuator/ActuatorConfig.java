package com.company.hrms.common.infrastructure.actuator;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Actuator 可觀測性共用配置
 *
 * <p>提供：
 * <ul>
 *   <li>自訂 Kafka 健康檢查指標</li>
 *   <li>應用程式 info 資訊（版本、啟動時間）</li>
 * </ul>
 *
 * <p>所有微服務透過 hrms-common 自動載入此配置。
 */
@Slf4j
@Configuration
public class ActuatorConfig {

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Value("${spring.kafka.bootstrap-servers:#{null}}")
    private String kafkaBootstrapServers;

    /**
     * Kafka 連線狀態健康檢查
     *
     * <p>僅在 classpath 有 Kafka AdminClient 且設定了 bootstrap-servers 時啟用。
     * 透過 AdminClient.describeCluster 檢查 Kafka broker 是否可達。
     */
    @Bean("kafkaHealthIndicator")
    @ConditionalOnClass(name = "org.apache.kafka.clients.admin.AdminClient")
    @ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
    public HealthIndicator kafkaHealthIndicator() {
        return () -> {
            if (kafkaBootstrapServers == null || kafkaBootstrapServers.isBlank()) {
                return Health.unknown().withDetail("reason", "未設定 Kafka bootstrap-servers").build();
            }

            Properties props = new Properties();
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
            // 設定較短的超時避免健康檢查阻塞
            props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
            props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 5000);

            try (AdminClient adminClient = AdminClient.create(props)) {
                // 嘗試取得叢集資訊，驗證連線
                String clusterId = adminClient.describeCluster()
                        .clusterId()
                        .get(5, TimeUnit.SECONDS);

                int nodeCount = adminClient.describeCluster()
                        .nodes()
                        .get(5, TimeUnit.SECONDS)
                        .size();

                return Health.up()
                        .withDetail("clusterId", clusterId)
                        .withDetail("bootstrapServers", kafkaBootstrapServers)
                        .withDetail("nodeCount", nodeCount)
                        .build();
            } catch (Exception e) {
                log.warn("Kafka 健康檢查失敗: {}", e.getMessage());
                return Health.down()
                        .withDetail("bootstrapServers", kafkaBootstrapServers)
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * 應用程式 info 資訊
     *
     * <p>在 /actuator/info 端點中顯示：
     * <ul>
     *   <li>應用程式名稱</li>
     *   <li>版本號</li>
     *   <li>JVM 啟動時間</li>
     *   <li>運行時間</li>
     * </ul>
     */
    @Bean
    public InfoContributor applicationInfoContributor() {
        // 啟動時間在 Bean 建立時記錄
        final Instant startTime = Instant.ofEpochMilli(
                ManagementFactory.getRuntimeMXBean().getStartTime());

        return builder -> {
            Duration uptime = Duration.between(startTime, Instant.now());
            String formattedStartTime = DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(startTime);

            builder.withDetail("app", Map.of(
                    "name", applicationName,
                    "version", "1.0.0-SNAPSHOT",
                    "startTime", formattedStartTime,
                    "uptime", formatDuration(uptime),
                    "java", System.getProperty("java.version", "unknown"),
                    "jvm", System.getProperty("java.vm.name", "unknown")
            ));
        };
    }

    /**
     * 將 Duration 格式化為人類可讀字串
     */
    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
}
