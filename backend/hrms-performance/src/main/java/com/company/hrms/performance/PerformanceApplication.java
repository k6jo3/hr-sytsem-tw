package com.company.hrms.performance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS Performance Service Application
 * Domain Code: HR08
 */
@SpringBootApplication(scanBasePackages = {
    "com.company.hrms.performance",
    "com.company.hrms.common"
})
@EnableDiscoveryClient
public class PerformanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PerformanceApplication.class, args);
    }
}
