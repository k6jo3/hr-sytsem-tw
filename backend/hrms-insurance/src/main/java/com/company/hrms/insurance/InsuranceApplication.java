package com.company.hrms.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS Insurance Service Application
 * Domain Code: HR05
 */
@SpringBootApplication(scanBasePackages = {
    "com.company.hrms.insurance",
    "com.company.hrms.common"
})
@EnableDiscoveryClient
public class InsuranceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InsuranceApplication.class, args);
    }
}
