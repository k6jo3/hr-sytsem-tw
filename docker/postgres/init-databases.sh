#!/bin/bash
# 自動建立所有微服務的 database
# 此腳本由 PG 容器啟動時自動執行（/docker-entrypoint-initdb.d/）

set -e

DATABASES=(
  hrms_iam
  hrms_organization
  hrms_attendance
  hrms_payroll
  hrms_insurance
  hrms_project
  hrms_timesheet
  hrms_performance
  hrms_recruitment
  hrms_training
  hrms_workflow
  hrms_notification
  hrms_document
  hrms_reporting
)

for DB in "${DATABASES[@]}"; do
  echo "Creating database: $DB"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    SELECT 'CREATE DATABASE $DB'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$DB')\gexec
EOSQL
done

echo "All databases created successfully."
