-- Create all HRMS databases
CREATE DATABASE hrms_iam;
CREATE DATABASE hrms_organization;
CREATE DATABASE hrms_attendance;
CREATE DATABASE hrms_payroll;
CREATE DATABASE hrms_insurance;
CREATE DATABASE hrms_project;
CREATE DATABASE hrms_timesheet;
CREATE DATABASE hrms_performance;
CREATE DATABASE hrms_recruitment;
CREATE DATABASE hrms_training;
CREATE DATABASE hrms_workflow;
CREATE DATABASE hrms_notification;
CREATE DATABASE hrms_document;
CREATE DATABASE hrms_reporting;

-- Grant privileges to hrms_user
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
