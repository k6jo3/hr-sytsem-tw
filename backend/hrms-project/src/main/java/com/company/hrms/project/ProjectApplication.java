package com.company.hrms.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS Project Service Application
 * Domain Code: HR06
 */
@SpringBootApplication(scanBasePackages = {
    "com.company.hrms.project",
    "com.company.hrms.common"
})
@EnableDiscoveryClient
public class ProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }
}
