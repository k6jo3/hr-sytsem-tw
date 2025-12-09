package com.company.hrms.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS Organization Service Application
 * Domain Code: HR02
 */
@SpringBootApplication(scanBasePackages = {
    "com.company.hrms.organization",
    "com.company.hrms.common"
})
@EnableDiscoveryClient
public class OrganizationApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrganizationApplication.class, args);
    }
}
