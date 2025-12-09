package com.company.hrms.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HRMS IAM Service Application
 * Domain Code: HR01
 * 
 * Identity & Access Management Service
 * - 身份認證 (Authentication)
 * - 授權管理 (Authorization)
 * - 角色權限 (RBAC)
 * - SSO整合 (Single Sign-On)
 * - 多租戶管理 (Multi-tenancy)
 */
@SpringBootApplication(scanBasePackages = {
    "com.company.hrms.iam",
    "com.company.hrms.common"
})
@EnableDiscoveryClient
public class IamApplication {
    public static void main(String[] args) {
        SpringApplication.run(IamApplication.class, args);
    }
}
