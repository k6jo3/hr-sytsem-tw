package com.company.hrms.timesheet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
/**
 * HRMS Timesheet Service Application
 * Domain Code: HR07
 */
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "com.company.hrms.timesheet",
        "com.company.hrms.common"
})
@EnableDiscoveryClient
@EnableFeignClients
public class TimesheetApplication {
    public static void main(String[] args) {
        SpringApplication.run(TimesheetApplication.class, args);
    }
}
