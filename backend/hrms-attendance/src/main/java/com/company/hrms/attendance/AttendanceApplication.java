package com.company.hrms.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * HRMS Attendance Service Application
 * Domain Code: HR03
 */
@SpringBootApplication(scanBasePackages = {
        "com.company.hrms.attendance",
        "com.company.hrms.common"
})
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class AttendanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
    }
}
