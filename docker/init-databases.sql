-- ============================================================
-- HRMS 微服務資料庫初始化腳本
-- 用途：Docker PostgreSQL 容器啟動時自動建立所有微服務資料庫
-- 說明：此腳本由 docker-entrypoint-initdb.d 機制自動執行
--       POSTGRES_DB=hrms_iam 已由容器環境變數建立，此處建立其餘 13 個
-- ============================================================

-- HR02 組織員工服務
CREATE DATABASE hrms_organization;

-- HR03 考勤管理服務
CREATE DATABASE hrms_attendance;

-- HR04 薪資管理服務
CREATE DATABASE hrms_payroll;

-- HR05 保險管理服務
CREATE DATABASE hrms_insurance;

-- HR06 專案管理服務
CREATE DATABASE hrms_project;

-- HR07 工時管理服務
CREATE DATABASE hrms_timesheet;

-- HR08 績效管理服務
CREATE DATABASE hrms_performance;

-- HR09 招募管理服務
CREATE DATABASE hrms_recruitment;

-- HR10 訓練管理服務
CREATE DATABASE hrms_training;

-- HR11 簽核流程服務
CREATE DATABASE hrms_workflow;

-- HR12 通知服務
CREATE DATABASE hrms_notification;

-- HR13 文件管理服務
CREATE DATABASE hrms_document;

-- HR14 報表分析服務
CREATE DATABASE hrms_reporting;

-- ============================================================
-- 授予 hrms_user 對所有資料庫的完整權限
-- 注意：hrms_iam 已由 POSTGRES_DB 建立，owner 即為 hrms_user
-- ============================================================
GRANT ALL PRIVILEGES ON DATABASE hrms_organization TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_attendance TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_payroll TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_insurance TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_project TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_timesheet TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_performance TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_recruitment TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_training TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_workflow TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_notification TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_document TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_reporting TO hrms_user;
