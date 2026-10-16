# 医疗陪护系统

医疗陪护系统是一套面向患者、家属、陪护师和运营人员的陪护服务平台，包含移动端、Web 管理端和统一后端服务。系统支持用户注册登录、陪护师浏览、订单管理、权限管理及医院数据展示等功能。

## 在线访问

| 端 | 地址 | 说明 |
|---|---|---|
| 移动端 | [http://124.220.16.162:5050](http://124.220.16.162:5050) | 患者/用户使用的 H5 页面 |
| Web 端 | [http://124.220.16.162:80](http://124.220.16.162:80) | 管理后台 |

> Web 服务使用 HTTP 80 端口，浏览器中也可以直接访问 `http://124.220.16.162`。

## 项目结构

```text
MedicalCompanionSystem/
├── mc_h5/                         # 移动端 H5（Vue 3 + Vant）
├── mc_ui/                         # Web 管理端（Vue 3 + Element Plus）
├── mc_server/                     # 后端服务（Spring Boot）
├── docker-compose.yml             # Docker 一键部署编排
├── Dockerfile.nginx               # 前端 Nginx 镜像
├── nginx.conf                     # Nginx 反向代理配置
├── 宝塔部署指南.md                # 宝塔部署说明
└── Docker部署指南.md              # Docker 部署说明
```

## 功能概览

### 移动端（`mc_h5`）

- 用户注册、登录和身份认证
- 首页及医院信息展示
- 陪护师浏览与选择
- 创建订单、查看订单详情和订单状态
- 个人中心及用户信息管理

### Web 管理端（`mc_ui`）

- 管理后台登录与权限校验
- 数据仪表盘
- 陪护师及陪护师头像管理
- 订单列表、详情和订单状态管理
- 用户、管理员和权限组管理
- 菜单权限配置

### 后端（`mc_server`）

- REST API 及统一 `/api` 上下文路径
- JWT 登录认证与接口权限控制
- MySQL 数据持久化
- Redis 缓存
- 短信验证码服务
- 高德地图医院/POI 数据查询

## 技术栈

| 模块 | 主要技术 |
|---|---|
| 移动端 | Vue 3、TypeScript、Vite、Vant、Vue Router |
| Web 端 | Vue 3、TypeScript、Vite、Element Plus、ECharts、Pinia |
| 后端 | Java 21、Spring Boot、MyBatis、MyBatis-Plus、JWT |
| 数据与基础设施 | MySQL 8、Redis 7、Nginx、Docker Compose |

## 本地运行

### 环境要求

- Node.js 20.19+（或 22.12+）
- Java 21+
- Maven 3.9+
- MySQL 8+
- Redis 7+

### 1. 启动后端

先创建 `mc_sys` 数据库并执行 `mc_server/src/main/resources/db/mc_sys_init.sql`，然后启动 Redis 和后端服务：

```bash
cd mc_server
mvn clean package -DskipTests
java -jar target/mc_server-0.0.1-SNAPSHOT.jar
```

后端默认监听 `8080` 端口，接口前缀为 `/api`。数据库、Redis、短信和地图服务配置请根据实际环境填写，不要把真实凭证提交到公开仓库。

### 2. 启动 Web 管理端

```bash
cd mc_ui
npm install
npm run dev
```

### 3. 启动移动端

```bash
cd mc_h5
npm install
npm run dev
```

移动端 Vite 开发服务器默认使用 `5050` 端口；两个前端项目都会把 `/api` 请求代理到本地后端 `8080` 端口。

## 构建发布

```bash
# Web 管理端
cd mc_ui
npm install
npm run build

# 移动端
cd ../mc_h5
npm install
npm run build
```

构建产物分别位于 `mc_ui/dist/` 和 `mc_h5/dist/`。生产环境可使用 Nginx 托管静态文件，并将 `/api` 反向代理到后端服务。

## 部署文档

- [宝塔部署指南](./宝塔部署指南.md)
- [Docker 部署指南](./Docker部署指南.md)

## 安全提示

部署到公开服务器前，请替换数据库密码、短信服务密钥、地图 API Key 和 JWT 相关配置，并优先通过环境变量或服务器侧配置注入敏感信息。线上服务建议使用 HTTPS、限制数据库和 Redis 的公网访问，并在防火墙中只开放必要端口。

