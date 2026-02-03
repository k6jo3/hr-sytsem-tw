package com.company.hrms.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * HRMS Workflow Service Application
 * Domain Code: HR11
 */
@SpringBootApplication(scanBasePackages = {
        "com.company.hrms.workflow",
        "com.company.hrms.common"
})
@EnableDiscoveryClient
@EnableFeignClients
public class WorkflowApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }
}
