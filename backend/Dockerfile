## ---------- Build stage ----------
FROM maven:3.9.6-openjdk-21-slim AS builder

WORKDIR /app

# 复制Maven配置
COPY pom.xml ./
COPY backend/pom.xml ./backend/

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源码
COPY backend/src ./backend/src

# 构建应用
RUN mvn clean package -f backend/pom.xml -DskipTests

## ---------- Runtime stage ----------
FROM openjdk:21-jre-slim

# 设置工作目录
WORKDIR /app

# 安装必要的工具
RUN apt-get update && apt-get install -y \
  curl \
  && rm -rf /var/lib/apt/lists/*

# 创建非root用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 复制jar包
COPY --from=builder /app/backend/target/*.jar app.jar

# 设置权限
RUN chown -R appuser:appuser /app
USER appuser

# 暴露端口
EXPOSE 9999

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:9999/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
