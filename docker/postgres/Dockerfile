# 自訂 PostgreSQL image — 自動建立所有微服務的 database
FROM postgres:15-alpine

# 複製初始化腳本到 docker-entrypoint-initdb.d/
# PG 容器啟動時會自動執行這些腳本
COPY init-databases.sh /docker-entrypoint-initdb.d/01-init-databases.sh
COPY seed-iam.sql /docker-entrypoint-initdb.d/02-seed-iam.sql

RUN chmod +x /docker-entrypoint-initdb.d/01-init-databases.sh
