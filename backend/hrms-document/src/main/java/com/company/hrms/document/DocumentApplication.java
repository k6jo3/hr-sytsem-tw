package com.company.hrms.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS Document Service Application
 * Domain Code: HR13
 */
@SpringBootApplication(scanBasePackages = {
    "com.company.hrms.document",
    "com.company.hrms.common"
})
@EnableDiscoveryClient
public class DocumentApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocumentApplication.class, args);
    }
}
