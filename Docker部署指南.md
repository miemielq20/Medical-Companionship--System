# 医疗陪护系统 - Docker 部署指南

> 服务器公网IP：124.220.16.162  
> 更新时间：2026-05-26

---

## 一、为什么用 Docker

### Docker vs 宝塔

| 维度 | Docker | 宝塔面板 |
|---|---|---|
| 上手难度 | 需学 Dockerfile、Compose | 图形化界面，门槛低 |
| 环境隔离 | 容器级隔离，互不干扰 | 共用系统环境，可能冲突 |
| 一致性 | 开发/测试/生产完全一致 | 不同服务器环境可能不同 |
| 迁移 | 打包镜像，一条命令在任何机器启动 | 新服务器需重新安装配置环境 |
| 版本管理 | 镜像 Tag 精确控制版本，回滚方便 | 手动管理 JAR 包 |
| 适用场景 | 中大型项目、团队协作、微服务 | 个人项目、单机快速上线 |

### 本项目容器化方案

| 服务 | 容器 |
|---|---|
| Spring Boot 后端 | `mc_backend`（端口 8080） |
| MySQL 8.0 | `mc_mysql`（端口 3306） |
| Redis 7 | `mc_redis`（端口 6379） |
| Nginx + 前端 | `mc_frontend`（端口 80 / 8081） |

---

## 二、安装 Docker

SSH 连接服务器 `124.220.16.162`，逐条执行：

```bash
# 1. 更新软件包
sudo apt update

# 2. 安装依赖
sudo apt install -y ca-certificates curl

# 3. 添加 Docker 官方 GPG 密钥
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# 4. 添加 Docker 软件源
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 5. 安装
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 6. 验证
docker --version
docker compose version
```

### 配置国内镜像加速（Docker Hub 被墙）

```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://docker.1ms.run",
    "https://docker.xuanyuan.me"
  ]
}
EOF

sudo systemctl daemon-reload
sudo systemctl restart docker
```

验证加速器：

```bash
docker info | grep -A 5 "Registry Mirrors"
```

### 解决权限问题

```bash
sudo usermod -aG docker $USER
# ⚠️ 执行后必须退出 SSH 重新登录才生效
```

---

## 三、项目文件结构

```
MedicalCompanionSystem/
├── mc_server/
│   ├── Dockerfile                  ← 后端容器
│   ├── target/
│   │   └── mc_server-0.0.1-SNAPSHOT.jar
│   └── src/main/resources/db/
│       └── mc_sys_init.sql         ← 数据库初始化脚本
├── mc_ui/
│   └── dist/                       ← 管理后台构建产物
├── mc_h5/
│   └── dist/                       ← 移动端构建产物
├── nginx.conf                      ← Nginx 配置
├── Dockerfile.nginx                ← 前端容器
└── docker-compose.yml              ← 一键编排
```

---

## 四、配置文件详解

### 4.1 后端 Dockerfile

**文件**：`mc_server/Dockerfile`

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/mc_server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

| 行 | 说明 |
|---|---|
| `FROM eclipse-temurin:21-jre` | 基于 Java 21 JRE 精简镜像 |
| `COPY ... app.jar` | 把本地构建好的 JAR 拷进容器 |
| `EXPOSE 8080` | 声明容器监听 8080 端口 |
| `ENTRYPOINT` | 启动时执行的命令 |

> **为什么不把 Maven 编译也放进 Dockerfile？**  
> 编译在本地 `mvn package` 就完成了，Docker 只管"拿 JAR + 配 JRE + 运行"。  
> 多阶段构建（容器里编译）适合 CI/CD 流水线，本地部署用这种方式更快更简洁。

---

### 4.2 前端 Dockerfile

**文件**：`Dockerfile.nginx`

```dockerfile
FROM nginx:alpine
COPY mc_ui/dist /usr/share/nginx/html/mc_ui
COPY mc_h5/dist /usr/share/nginx/html/mc_h5
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80 8081
```

| 行 | 说明 |
|---|---|
| `FROM nginx:alpine` | 基于 Alpine 版 Nginx（仅 ~10MB） |
| `COPY mc_ui/dist` | 管理后台静态文件 |
| `COPY mc_h5/dist` | 移动端静态文件 |
| `COPY nginx.conf` | 替换为自定义配置 |

---

### 4.3 Nginx 配置

**文件**：`nginx.conf`

```nginx
worker_processes auto;

events {
    worker_connections 1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile      on;
    keepalive_timeout 65;

    # 管理后台 (mc_ui) — 端口 80
    server {
        listen 80;
        server_name _;

        location / {
            root   /usr/share/nginx/html/mc_ui;
            index  index.html;
            try_files $uri $uri/ /index.html;
        }

        location /api {
            proxy_pass http://backend:8080;   # ⚠️ 用 Docker 服务名，不是 127.0.0.1
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }

    # 移动端 (mc_h5) — 端口 8081
    server {
        listen 8081;
        server_name _;

        location / {
            root   /usr/share/nginx/html/mc_h5;
            index  index.html;
            try_files $uri $uri/ /index.html;
        }

        location /api {
            proxy_pass http://backend:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

> ⚠️ Docker 容器之间**不能用 `127.0.0.1`**，要用 **Docker 服务名**（这里是 `backend`）。  
> Docker Compose 会自动创建内部网络，服务名自动解析为对应容器的 IP。

---

### 4.4 编排文件

**文件**：`docker-compose.yml`

```yaml
services:
  # ========== MySQL ==========
  mysql:
    image: mysql:8.0
    container_name: mc_mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: aZhTnTG97XKX4JbY
      MYSQL_DATABASE: mc_sys
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql   # 数据持久化
      - ./mc_server/src/main/resources/db/mc_sys_init.sql:/docker-entrypoint-initdb.d/init.sql  # 自动建表
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

  # ========== Redis ==========
  redis:
    image: redis:7-alpine
    container_name: mc_redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  # ========== Spring Boot 后端 ==========
  backend:
    build:
      context: ./mc_server
      dockerfile: Dockerfile
    container_name: mc_backend
    restart: always
    ports:
      - "8080:8080"
    environment:
      # ⚠️ 覆盖 application.properties 中的 localhost 为 Docker 服务名
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/mc_sys?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: aZhTnTG97XKX4JbY
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
      TZ: Asia/Shanghai
    depends_on:
      mysql:
        condition: service_started
      redis:
        condition: service_started

  # ========== Nginx 前端 ==========
  frontend:
    build:
      context: .
      dockerfile: Dockerfile.nginx
    container_name: mc_frontend
    restart: always
    ports:
      - "80:80"
      - "8081:8081"
    depends_on:
      - backend

volumes:
  mysql_data:
  redis_data:
```

### 关键机制说明

#### 1. 为什么用环境变量覆盖配置？

本地 `application.properties` 中 MySQL 地址是 `localhost:3306`。但在 Docker 里，每个容器是一台"独立小电脑"。

```
❌ 错误认知：容器里写 localhost 能连 MySQL
✅ 正确理解：后端容器里的 localhost 是它自己，不是 MySQL 容器
```

用 `SPRING_DATASOURCE_URL` 环境变量，Spring Boot 自动映射到 `spring.datasource.url`：

```
SPRING_DATASOURCE_URL → spring.datasource.url
SPRING_DATA_REDIS_HOST → spring.data.redis.host
```

#### 2. 自动初始化数据库

```yaml
volumes:
  - ./mc_sys_init.sql:/docker-entrypoint-initdb.d/init.sql
```

MySQL 容器首次启动时自动执行 `init.sql`，不用手动导入。

#### 3. depends_on 启动顺序

```
mysql + redis 先启动 → backend 再启动 → frontend 最后启动
```

#### 4. 数据卷（重启不丢数据）

```yaml
volumes:
  mysql_data:    # MySQL 数据
  redis_data:    # Redis 数据
```

即使容器删了重建，数据还在。

---

## 五、构建与启动

### 5.0 启动前准备

```bash
# 停掉占用端口的旧服务
kill -9 $(lsof -t -i:8080)  # 旧 Java 进程
systemctl stop mysql         # 旧 MySQL（如有）
systemctl stop redis         # 旧 Redis（如有）
nginx -s stop                # 旧 Nginx（如有）
```

### 5.1 上传文件到服务器

将本地项目文件按以下结构上传到 `/www/wwwroot/mc_system/`：

```
mc_system/
├── docker-compose.yml
├── Dockerfile.nginx
├── nginx.conf
├── mc_server/
│   ├── Dockerfile
│   ├── target/mc_server-0.0.1-SNAPSHOT.jar
│   └── src/main/resources/db/mc_sys_init.sql
├── mc_ui/dist/
└── mc_h5/dist/
```

### 5.2 一条命令启动

```bash
cd /www/wwwroot/mc_system
docker compose up -d --build
```

| 参数 | 作用 |
|---|---|
| `up` | 启动所有服务 |
| `-d` | 后台运行 |
| `--build` | 先构建镜像再启动 |

### 5.3 查看状态

```bash
docker compose ps
```

四个容器都显示 `Up` 即成功：

```
NAME          STATUS
mc_mysql      Up
mc_redis      Up
mc_backend    Up
mc_frontend   Up
```

---

## 六、访问地址

| 项目 | 地址 |
|---|---|
| 管理后台 | http://124.220.16.162 |
| 移动端 | http://124.220.16.162:8081 |

---

## 七、常用命令速查

| 命令 | 作用 |
|---|---|
| `docker compose ps` | 查看所有容器状态 |
| `docker compose logs` | 查看所有容器日志 |
| `docker compose logs backend` | 只看后端日志 |
| `docker compose logs -f` | 实时跟踪日志 |
| `docker compose restart backend` | 重启后端（修改配置后） |
| `docker compose down` | 停止并删除所有容器 |
| `docker compose up -d` | 重新启动（不改代码时不用 `--build`） |
| `docker compose up -d --build` | 重新构建镜像并启动 |
| `docker compose down -v` | 停止并**删除数据卷**（⚠️ 数据会丢） |
| `docker exec -it mc_mysql mysql -uroot -p` | 进入 MySQL 容器 |
| `docker exec -it mc_backend sh` | 进入后端容器调试 |

---

## 八、踩坑记录 & 问题排查

### 1. Docker Hub 镜像拉取超时

**现象**：
```
Error: dial tcp ...:443: i/o timeout
```

**原因**：Docker Hub 在国内被墙。

**解决**：配置国内镜像加速器（见第二章）。

---

### 2. 权限拒绝

**现象**：
```
permission denied while trying to connect to the docker API
```

**解决**：
```bash
sudo usermod -aG docker $USER
# 退出 SSH 重新登录
```

---

### 3. 后端连不上 MySQL / Redis

**现象**：后端日志报 `Connection refused: localhost:3306`

**原因**：在 Docker 里 `localhost` 指的是容器自己，不是 MySQL 容器。

**解决**：用 Docker 服务名，通过环境变量覆盖：

```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/mc_sys?...   # ✅ "mysql"，不是 localhost
SPRING_DATA_REDIS_HOST: redis                                # ✅ "redis"
```

---

### 4. 端口冲突

**现象**：
```
Error: port is already allocated
```

**解决**：停掉占用端口的旧服务。

```bash
lsof -i :8080   # 查看谁在用
lsof -i :3306
lsof -i :6379
```

---

### 5. 前端登录 404

**原因**：Nginx 的 `proxy_pass` 尾部不能有斜杠。

```nginx
# ❌ 错误（截掉 /api 前缀）
proxy_pass http://backend:8080/;

# ✅ 正确（保留 /api 前缀）
proxy_pass http://backend:8080;
```

---

### 6. 排查顺序

出问题时按以下顺序排查：

```bash
# 1. 容器都起来了吗？
docker compose ps

# 2. 后端日志有没有报错？
docker compose logs backend | tail -50

# 3. 后端能否连接数据库？
docker exec mc_backend sh -c "apt-get update && apt-get install -y curl && curl http://mysql:3306"

# 4. Nginx 能代理到后端吗？
docker exec mc_frontend sh -c "wget -qO- http://backend:8080/api/Index/index"
```

---

## 九、请求链路

```
浏览器
  │
  ├─ http://IP:80 ──→ Nginx 容器 (mc_frontend)
  │                     ├── /         → mc_ui 静态文件
  │                     └── /api/*    → 后端容器 (mc_backend:8080)
  │                                       ├── MySQL 容器 (mc_mysql:3306)
  │                                       └── Redis 容器 (mc_redis:6379)
  │
  └─ http://IP:8081 ─→ Nginx 容器 (mc_frontend)
                        ├── /         → mc_h5 静态文件
                        └── /api/*    → 后端容器 (mc_backend:8080)
```
