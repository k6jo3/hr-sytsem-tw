package com.company.hrms.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * IAM 微服務啟動類別
 * 身份認證與授權服務 (Identity & Access Management)
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.company.hrms.iam", "com.company.hrms.common"})
public class IamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }
}
