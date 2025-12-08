package com.company.hrms.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS Notification Service Application
 * Domain Code: HR12
 */
@SpringBootApplication(scanBasePackages = {
    "com.company.hrms.notification",
    "com.company.hrms.common"
})
@EnableDiscoveryClient
public class NotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
}
