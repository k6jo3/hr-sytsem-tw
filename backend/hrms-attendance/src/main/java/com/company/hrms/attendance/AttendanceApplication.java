package com.company.hrms.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS Attendance Service Application
 * Domain Code: HR03
 */
@SpringBootApplication(scanBasePackages = {
    "com.company.hrms.attendance",
    "com.company.hrms.common"
})
@EnableDiscoveryClient
public class AttendanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
    }
}
