# DynamicTp Admin 后台管理系统

[![SpringBoot](https://img.shields.io/badge/Spring%20Boot-3.5-blue.svg)](https://spring.io/projects/spring-boot)
[![JDK](https://img.shields.io/badge/JDK-21+-blue.svg)](https://adoptium.net/)
[![Version](https://img.shields.io/badge/Version-1.0.6--SNAPSHOT-blue.svg)](https://github.com/paynezhuang/panis-boot)
[![License](https://img.shields.io/badge/License-Apache%20License%202.0-B9D6AF.svg)](./LICENSE)
[![Author](https://img.shields.io/badge/Author-paynezhuang-green.svg)](https://github.com/paynezhuang)
[![Copyright](https://img.shields.io/badge/Copyright-2024%20Zhuang%20Pan%20@PanisBoot-green.svg)](https://github.com/paynezhuang)

## 📖 项目简介

**DynamicTp Admin** 是一个基于 Spring Boot 3.5 和 Vue 3 的现代化企业级后台管理系统，采用前后端分离架构设计。项目集成了当前主流的技术栈，提供了完整的权限管理、系统监控、业务管理等功能模块，旨在为企业提供高效、稳定、易扩展的后台管理解决方案。

### ✨ 核心特性

- 🚀 **现代化技术栈**: 基于 Spring Boot 3.5 + JDK 21 + Vue 3 + TypeScript
- 🏗️ **分层架构设计**: 采用 DDD 领域驱动设计，模块化架构清晰
- 🔐 **完善权限体系**: 基于 Sa-Token 的 RBAC 权限控制
- 📊 **实时监控系统**: 系统性能监控、线程池监控、任务调度监控
- 🎨 **优雅前端界面**: 基于 Naive UI 的现代化管理界面
- 🌍 **国际化支持**: 支持中英文双语切换
- 📱 **响应式设计**: 支持桌面端和移动端自适应
- 🔧 **开发工具链**: 完整的开发、测试、部署工具链

## 🏗️ 系统架构

### 后端架构

```
DynamicTp Admin
├── panis-boot-common          # 基础公共模块
│   ├── 工具类库               # 通用工具类、常量、异常定义
│   ├── 基础组件               # 基础组件和通用功能
│   └── 公共配置               # 公共配置和常量
├── panis-boot-infrastructure  # 基础设施模块
│   ├── 数据访问层             # MyBatis-Plus 配置、数据源配置
│   ├── 缓存管理               # Redis 缓存配置和管理
│   ├── 安全认证               # Sa-Token 安全框架配置
│   └── 系统配置               # 系统级配置和基础设施
├── panis-boot-modules         # 业务模块
│   ├── system                 # 系统管理模块
│   │   ├── domain            # 领域模型层
│   │   ├── service           # 业务服务层
│   │   ├── facade            # 门面层
│   │   └── repository        # 数据访问层
│   ├── monitor               # 系统监控模块
│   │   ├── 性能监控          # 系统性能指标监控
│   │   ├── 线程池监控        # 动态线程池监控
│   │   └── 任务调度监控      # Quartz 任务调度监控
│   ├── manager               # 业务管理模块
│   └── tools                 # 工具模块
├── panis-boot-admin           # 后台管理启动模块
│   ├── 控制器层               # REST API 接口
│   ├── 配置管理               # 应用配置和启动配置
│   └── 主启动类               # Spring Boot 启动入口
└── test                       # 测试模块
```

### 前端架构

```
frontend/
├── src/
│   ├── components/            # 通用组件
│   │   ├── common/           # 基础公共组件
│   │   ├── custom/           # 自定义组件
│   │   └── advanced/         # 高级组件
│   ├── layouts/              # 布局组件
│   │   ├── base-layout/      # 基础布局
│   │   ├── global-header/    # 全局头部
│   │   ├── global-menu/      # 全局菜单
│   │   └── global-sider/     # 全局侧边栏
│   ├── modules/              # 业务模块
│   │   ├── thread-pool/      # 线程池管理
│   │   └── ...               # 其他业务模块
│   ├── store/                # 状态管理
│   ├── router/               # 路由配置
│   ├── service/              # API 服务
│   └── utils/                # 工具函数
├── packages/                  # 内部包
│   ├── alova/                # HTTP 客户端
│   ├── axios/                # Axios 封装
│   ├── hooks/                # 通用 Hooks
│   ├── utils/                # 工具库
│   └── ...                   # 其他内部包
```

## 🛠️ 技术选型

### 后端技术栈

| 技术组件         | 版本    | 说明          |
| :--------------- | :------ | :------------ |
| **Spring Boot**  | 3.5.0   | 核心框架      |
| **JDK**          | 21+     | Java 运行环境 |
| **MyBatis-Plus** | 3.5.8   | ORM 框架      |
| **MySQL**        | 8.4.0   | 主数据库      |
| **Redis**        | 7.2.3   | 缓存数据库    |
| **Sa-Token**     | 1.43.0  | 安全认证框架  |
| **Knife4j**      | 4.5.0   | API 文档工具  |
| **Quartz**       | -       | 任务调度框架  |
| **Druid**        | -       | 数据库连接池  |
| **Logback**      | 1.5.18  | 日志管理      |
| **Lombok**       | 1.18.38 | 代码生成工具  |
| **Hutool**       | 5.8.38  | 工具库        |

### 前端技术栈

| 技术组件       | 版本    | 说明             |
| :------------- | :------ | :--------------- |
| **Vue**        | 3.5.17  | 前端框架         |
| **TypeScript** | 5.8.3   | 类型系统         |
| **Vite**       | 7.0.0   | 构建工具         |
| **Naive UI**   | 2.42.0  | UI 组件库        |
| **Pinia**      | 3.0.3   | 状态管理         |
| **Vue Router** | 4.5.1   | 路由管理         |
| **UnoCSS**     | 0.66.3  | CSS 框架         |
| **ECharts**    | 5.6.0   | 图表库           |
| **Day.js**     | 1.11.13 | 日期处理         |
| **VueUse**     | 13.4.0  | Vue 组合式函数库 |

## 🚀 快速开始

### 环境要求

- **Java**: JDK 21+
- **Node.js**: 20.19.0+
- **Maven**: 3.9.6+
- **MySQL**: 8.0.35+
- **Redis**: 7.2.3+
- **IDE**: IntelliJ IDEA (推荐)

### 后端启动

1. **克隆项目**

```bash
git clone https://github.com/paynezhuang/panis-boot
git clone https://github.com/paynezhuang/panis-boot-starter
```

2. **数据库配置**

   - 创建数据库 `panis_boot`
   - 导入 SQL 文件（联系作者获取）

3. **配置文件修改**

   - 修改 `admin/src/main/resources/application-dev.yml`
   - 配置数据库和 Redis 连接信息

4. **启动应用**
   - 运行 `PanisBootApplication` 主类
   - 看到启动成功日志即表示启动完成

### 前端启动

1. **安装依赖**

```bash
cd frontend
pnpm install
```

2. **启动开发服务器**

```bash
pnpm dev
```

3. **构建生产版本**

```bash
pnpm build
```

## 📱 功能模块

### 🔐 系统管理

- **用户管理**: 用户增删改查、角色分配、状态管理
- **角色管理**: 角色权限配置、菜单权限分配
- **菜单管理**: 动态菜单配置、权限控制
- **部门管理**: 组织架构管理、部门层级关系
- **字典管理**: 系统字典配置、数据字典维护

### 📊 系统监控

- **性能监控**: CPU、内存、磁盘、网络监控
- **线程池监控**: 动态线程池配置、性能调优
- **任务调度**: Quartz 任务管理、执行日志
- **操作日志**: 用户操作记录、系统日志查询
- **登录日志**: 用户登录记录、安全审计

### 🛠️ 业务管理

- **通知平台**: 消息推送、通知管理
- **工具模块**: 常用工具集合、实用功能
- **线程池管理**: 线程池配置、性能监控

### 🎨 前端特性

- **主题切换**: 明暗主题、自定义主题
- **国际化**: 中英文双语支持
- **权限控制**: 基于角色的页面权限控制
- **响应式设计**: 支持多种设备尺寸
- **组件库**: 丰富的 UI 组件和业务组件

## 🔧 开发指南

### 项目结构说明

- **common**: 通用工具类、异常定义、常量等
- **infrastructure**: 基础设施配置、数据访问、缓存等
- **modules**: 业务模块，采用 DDD 架构设计
- **admin**: 应用启动模块，包含控制器和配置

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用统一的代码格式化配置
- 完善的注释和文档
- 单元测试覆盖

### API 文档

启动后端服务后，访问 `http://localhost:9999/doc.html` 查看 API 文档。

## 📦 部署说明

### Docker 部署

支持一键编排（MySQL + Redis + 后端 + 前端）：

```bash
# 启动（首次会自动构建镜像）
docker compose up -d --build

# 查看服务
docker compose ps

# 访问地址
# 后端 API: http://localhost:9999
# 前端界面: http://localhost
```

可选：自定义数据库/Redis 连接（在 `docker-compose.yml` 中修改或通过命令覆盖）：

```bash
DB_PASSWORD=yourpass \
docker compose up -d --build
```

如果仅单独构建后端或前端镜像：

```bash
# 后端（多阶段构建，自动打包 admin 模块）
docker build -f admin/Dockerfile -t panis-boot-admin:latest .

# 前端（多阶段构建，Nginx 托管静态资源）
docker build -f frontend/Dockerfile -t panis-boot-frontend:latest ./frontend
```

### 传统部署

1. 打包项目：`mvn clean package`
2. 上传 jar 包到服务器
3. 配置环境变量和启动脚本
4. 启动应用

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/AmazingFeature`
3. 提交更改：`git commit -m 'Add some AmazingFeature'`
4. 推送分支：`git push origin feature/AmazingFeature`
5. 提交 Pull Request

## 📄 开源协议

本项目基于 [Apache License 2.0](./LICENSE) 协议开源，仅供学习参考。商业使用请遵循作者版权信息，作者不保证也不承担任何软件的使用风险。

## 🙏 特别鸣谢

- [SoybeanJS](https://github.com/soybeanjs) - 优秀的前端模板
- [MyBatis-Plus](https://mybatis.plus/) - 强大的 ORM 框架
- [Sa-Token](https://sa-token.cc/) - 轻量级安全框架
- [Knife4j](https://doc.xiaominfo.com/) - API 文档工具
- [Naive UI](https://www.naiveui.com/) - Vue 3 组件库
- [Hutool](https://hutool.cn/) - Java 工具类库

感谢所有开源项目的贡献者！

## 📞 联系我们

- **作者**: EachannChan
- **邮箱**: eachannchan@qq.com
- **GitHub**: [https://github.com/EachannChan](https://github.com/EachannChan)
- **项目地址**: [https://github.com/dromara/dynamic-tp-admin](https://github.com/dromara/dynamic-tp-admin)

---

⭐ 如果这个项目对你有帮助，请给我们一个 Star！
