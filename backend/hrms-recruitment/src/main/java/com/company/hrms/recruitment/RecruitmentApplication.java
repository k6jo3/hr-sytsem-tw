package com.company.hrms.recruitment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS Recruitment Service Application
 * Domain Code: HR09
 */
@SpringBootApplication(scanBasePackages = {
        "com.company.hrms.recruitment",
        "com.company.hrms.common"
})
@EnableDiscoveryClient
@org.springframework.cloud.openfeign.EnableFeignClients(basePackages = "com.company.hrms.recruitment.infrastructure.client")
public class RecruitmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecruitmentApplication.class, args);
    }
}
