# 前端 Multi-stage Dockerfile

# ===== Stage 1: Build =====
FROM node:20-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci --ignore-scripts

COPY . .
RUN npm run build

# ===== Stage 2: Nginx =====
FROM nginx:alpine

# 複製自訂 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

# DEPLOY_ENV: "compose"（預設）或 "azure"
# azure 模式：用 resolver + k8s FQDN 動態解析 Gateway
ENV DEPLOY_ENV=compose

CMD ["/bin/sh", "-c", "\
  if [ \"$DEPLOY_ENV\" = \"azure\" ]; then \
    sed -i 's|proxy_pass http://hrms-gateway:8080;|resolver 127.0.0.11 valid=5s ipv6=off;\\n        set $backend \"http://hrms-gateway.k8se-apps.svc.cluster.local\";\\n        proxy_pass $backend;|' /etc/nginx/conf.d/default.conf; \
  fi && \
  nginx -g 'daemon off;'"]
