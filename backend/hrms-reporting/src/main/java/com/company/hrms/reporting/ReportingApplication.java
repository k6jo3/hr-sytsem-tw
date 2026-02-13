package com.company.hrms.reporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * HRMS Reporting Service Application
 * Domain Code: HR14
 */
@SpringBootApplication(scanBasePackages = {
        "com.company.hrms.reporting",
        "com.company.hrms.common"
})
@EnableDiscoveryClient
@EnableAsync
public class ReportingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportingApplication.class, args);
    }
}
