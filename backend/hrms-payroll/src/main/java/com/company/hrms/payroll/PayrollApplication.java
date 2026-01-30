package com.company.hrms.payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS Payroll Service Application
 * Domain Code: HR04
 */
@SpringBootApplication(scanBasePackages = {
        "com.company.hrms.payroll",
        "com.company.hrms.common"
})
@EnableDiscoveryClient
@org.springframework.cloud.openfeign.EnableFeignClients(basePackages = "com.company.hrms.payroll.infrastructure.client")
public class PayrollApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayrollApplication.class, args);
    }
}
