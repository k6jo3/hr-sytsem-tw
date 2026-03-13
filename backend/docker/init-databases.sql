-- 建立所有 HRMS 資料庫（跳過已存在的）
SELECT 'CREATE DATABASE hrms_iam' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_iam')\gexec
SELECT 'CREATE DATABASE hrms_organization' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_organization')\gexec
SELECT 'CREATE DATABASE hrms_attendance' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_attendance')\gexec
SELECT 'CREATE DATABASE hrms_payroll' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_payroll')\gexec
SELECT 'CREATE DATABASE hrms_insurance' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_insurance')\gexec
SELECT 'CREATE DATABASE hrms_project' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_project')\gexec
SELECT 'CREATE DATABASE hrms_timesheet' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_timesheet')\gexec
SELECT 'CREATE DATABASE hrms_performance' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_performance')\gexec
SELECT 'CREATE DATABASE hrms_recruitment' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_recruitment')\gexec
SELECT 'CREATE DATABASE hrms_training' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_training')\gexec
SELECT 'CREATE DATABASE hrms_workflow' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_workflow')\gexec
SELECT 'CREATE DATABASE hrms_notification' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_notification')\gexec
SELECT 'CREATE DATABASE hrms_document' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_document')\gexec
SELECT 'CREATE DATABASE hrms_reporting' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hrms_reporting')\gexec

-- 授予權限
GRANT ALL PRIVILEGES ON DATABASE hrms_iam TO hrms_user;
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

-- 為每個資料庫的 public schema 授權
DO $$
DECLARE
    db_name TEXT;
    db_names TEXT[] := ARRAY[
        'hrms_iam','hrms_organization','hrms_attendance','hrms_payroll',
        'hrms_insurance','hrms_project','hrms_timesheet','hrms_performance',
        'hrms_recruitment','hrms_training','hrms_workflow','hrms_notification',
        'hrms_document','hrms_reporting'
    ];
BEGIN
    FOREACH db_name IN ARRAY db_names
    LOOP
        EXECUTE format('GRANT ALL ON SCHEMA public TO hrms_user');
    END LOOP;
END
$$;
