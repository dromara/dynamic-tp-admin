## ---------- Build stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

# 仅复制POM文件以缓存依赖
COPY pom.xml ./
COPY common/pom.xml common/pom.xml
COPY infrastructure/pom.xml infrastructure/pom.xml
COPY modules/pom.xml modules/pom.xml
COPY admin/pom.xml admin/pom.xml
COPY test/pom.xml test/pom.xml

# 复制源码
COPY common/src common/src
COPY infrastructure/src infrastructure/src
COPY modules/src modules/src
COPY admin/src admin/src

# 构建 admin 模块（同时构建其依赖模块），跳过测试
RUN mvn -T 1C -DskipTests -pl admin -am clean package

## ---------- Runtime stage ----------
FROM eclipse-temurin:21.0.3_9-jre-alpine
LABEL maintainer="eachannchan@qq.com"

# 设置时区
ENV TZ='Asia/Shanghai'
RUN ln -snf /usr/share/zoneinfo/"$TZ" /etc/localtime && echo "$TZ" > /etc/timezone

# 设置工作目录
WORKDIR /app

# 复制构建产物（排除 .original）
COPY --from=builder /build/admin/target/panis-boot-admin-*.jar app.jar

# 允许外界访问容器的9999端口
EXPOSE 9999

# 配置容器启动后执行的命令
ENTRYPOINT ["java", "-jar", "app.jar"]