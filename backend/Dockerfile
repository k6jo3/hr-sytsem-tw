# 後端微服務 Multi-stage Dockerfile
# 用法：docker build --build-arg SERVICE=hrms-iam -t hrms-iam .

# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# 先複製 pom.xml 利用 Docker layer cache
COPY pom.xml .
COPY hrms-common/pom.xml hrms-common/pom.xml
COPY hrms-gateway/pom.xml hrms-gateway/pom.xml
COPY hrms-iam/pom.xml hrms-iam/pom.xml
COPY hrms-organization/pom.xml hrms-organization/pom.xml
COPY hrms-attendance/pom.xml hrms-attendance/pom.xml
COPY hrms-payroll/pom.xml hrms-payroll/pom.xml
COPY hrms-insurance/pom.xml hrms-insurance/pom.xml
COPY hrms-project/pom.xml hrms-project/pom.xml
COPY hrms-timesheet/pom.xml hrms-timesheet/pom.xml
COPY hrms-performance/pom.xml hrms-performance/pom.xml
COPY hrms-recruitment/pom.xml hrms-recruitment/pom.xml
COPY hrms-training/pom.xml hrms-training/pom.xml
COPY hrms-workflow/pom.xml hrms-workflow/pom.xml
COPY hrms-notification/pom.xml hrms-notification/pom.xml
COPY hrms-document/pom.xml hrms-document/pom.xml
COPY hrms-reporting/pom.xml hrms-reporting/pom.xml

# 下載依賴（利用 cache）
RUN mvn dependency:go-offline -B -q 2>/dev/null || true

# 複製全部原始碼
COPY . .

# Docker 容器內已有 JDK 21，替換 Windows 專用的 Toolchains 為容器內路徑
RUN echo '<?xml version="1.0" encoding="UTF-8"?><toolchains xmlns="http://maven.apache.org/TOOLCHAINS/1.1.0"><toolchain><type>jdk</type><provides><version>21</version></provides><configuration><jdkHome>/opt/java/openjdk</jdkHome></configuration></toolchain></toolchains>' > .mvn/toolchains.xml

# 編譯指定的服務模組（跳過測試加速建置）
ARG SERVICE=hrms-iam
RUN mvn package -pl ${SERVICE} -am -DskipTests -B -q

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S hrms && adduser -S hrms -G hrms

WORKDIR /app

ARG SERVICE=hrms-iam
COPY --from=builder /app/${SERVICE}/target/*.jar app.jar

RUN chown -R hrms:hrms /app
USER hrms

EXPOSE 8080

# JVM 記憶體限制：透過環境變數 JAVA_OPTS 覆蓋（預設 -Xmx256m）
# docker-compose 可設定 JAVA_OPTS="-Xmx512m" 來調整
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseG1GC -XX:MaxMetaspaceSize=128m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
