package com.company.hrms.training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * HRMS Training Service Application
 * Domain Code: HR10
 */
@SpringBootApplication(scanBasePackages = {
        "com.company.hrms.training",
        "com.company.hrms.common"
})
@EnableDiscoveryClient
@EnableFeignClients
public class TrainingApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingApplication.class, args);
    }
}
