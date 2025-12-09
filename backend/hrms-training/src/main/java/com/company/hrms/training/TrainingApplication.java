package com.company.hrms.training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS Training Service Application
 * Domain Code: HR10
 */
@SpringBootApplication(scanBasePackages = {
    "com.company.hrms.training",
    "com.company.hrms.common"
})
@EnableDiscoveryClient
public class TrainingApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingApplication.class, args);
    }
}
