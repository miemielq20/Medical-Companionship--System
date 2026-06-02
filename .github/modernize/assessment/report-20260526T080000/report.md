# 项目现代化评估报告

更新时间：2026-05-26T08:00:00Z

**简要摘要**

后端模块 `mc_server` 当前使用 Spring Boot 父 POM 版本 4.0.6（[mc_server/pom.xml](mc_server/pom.xml#L8)），Maven 属性 `java.version` 已设置为 `21`（[mc_server/pom.xml](mc_server/pom.xml#L30)），Dockerfile 也采用 Temurin 21 基础镜像（[mc_server/Dockerfile](mc_server/Dockerfile#L15)）。应用以 fat JAR 打包（产物：`mc_server/target/mc_server-0.0.1-SNAPSHOT.jar`），生产配置文件位于 [mc_server/target/classes/application-prod.properties](mc_server/target/classes/application-prod.properties#L1-L120)（示例：数据库连接在 [mc_server/target/classes/application-prod.properties](mc_server/target/classes/application-prod.properties#L9)，日志路径在 [mc_server/target/classes/application-prod.properties](mc_server/target/classes/application-prod.properties#L40)）。整体而言：当前仓库已针对 Java 21 设置，升级到“当前 LTS（Java 21）”无需内核级变更；若用户意图是更高未来 LTS（>21），需额外评估第三方库与 Jakarta 兼容性。

**关键发现（Top 6）**

- **Java 版本**：项目已设置 `java.version=21`，Dockerfile 与镜像均使用 Temurin 21（低阻塞）。
- **Spring Boot**：父 POM 为 `spring-boot-starter-parent:4.0.6`，意味着项目已迁移到 Spring Boot 4（Jakarta 命名空间），依赖需要与之兼容。
- **敏感配置**：生产配置中存在硬编码凭据与第三方密钥（见 [mc_server/target/classes/application-prod.properties](mc_server/target/classes/application-prod.properties#L9)），需要迁移至环境变量或密钥管理。
- **第三方兼容风险**：关键库（`mybatis-plus:3.5.5`、`pagehelper:1.4.7`、`jjwt:0.12.6` 等）可能存在与 Spring Boot 4 / Jakarta 迁移的兼容性问题，需逐一验证并升级或替换。
- **打包与运行方式**：使用 Spring Boot 打包为 fat JAR（`mc_server/target/mc_server-0.0.1-SNAPSHOT.jar`），Dockerfile 的 ENTRYPOINT 为 `java -jar app.jar`（[mc_server/Dockerfile](mc_server/Dockerfile#L24)），可直接在无 Docker 的宝塔主机通过安装 JRE 启动，也可使用 Docker 部署。
- **部署细节（Baota）**：配置日志写入 `/www/wwwlogs/mc_server`（[mc_server/target/classes/application-prod.properties](mc_server/target/classes/application-prod.properties#L40)）；宝塔环境需要创建该目录并设置权限，或在容器部署时挂载到宿主目录。数据库地址指向 `localhost`（默认 MySQL 在宝塔上可本地化），但需要确保账号/密码安全性与网络访问策略。

**必须手动更改或验证的文件与位置（每项一行）**

- [mc_server/pom.xml](mc_server/pom.xml#L30)：确认或更新 `java.version`（当前为 21）；若目标为未来 LTS（>21），在此处及父 POM/插件中调整。 
- [mc_server/pom.xml](mc_server/pom.xml#L7-L8)：确认 `spring-boot-starter-parent` 版本（当前 4.0.6），若升级 Spring Boot 版本需评估兼容性。 
- [mc_server/pom.xml](mc_server/pom.xml#L1-L120)：检查并显式固定关键依赖版本（例如 `mysql-connector-j` 未在 pom 中显式声明版本，依赖父 POM 管理；建议明确声明以控制版本）。
- [mc_server/target/classes/application-prod.properties](mc_server/target/classes/application-prod.properties#L9)：不要在生产配置中写入硬编码密码，改为使用环境变量（如 `SPRING_DATASOURCE_PASSWORD`）或外部化配置。 
- [mc_server/target/classes/application-prod.properties](mc_server/target/classes/application-prod.properties#L40)：确保日志目录 `/www/wwwlogs/mc_server` 在目标服务器存在且拥有正确权限，或改为可挂载路径。 
- [mc_server/Dockerfile](mc_server/Dockerfile#L15-L24)：如果将来要升级基础 JDK 镜像（到更高 LTS），需要在这里替换镜像标签并验证构建缓存与本地构建命令（`mvn package`）。

**打包 / 运行风险与建议**

- 风险：仓库包含生产密码与第三方密钥 → 必须尽快移除敏感信息并使用环境变量或密钥管理。 风险级别：高。
- 风险：第三方库与 Spring Boot 4（Jakarta）不兼容 → 逐一运行单元/集成测试并升级不兼容库。 风险级别：中-高（取决于库数量与耦合度）。
- 风险：宝塔上直接运行 Fat JAR 需系统安装 JRE 21；若不想手动安装，需使用 Docker（宝塔支持 Docker 插件）。 风险级别：低-中。

**对“升级到最新 LTS（用户意图）”的阻塞点**

1. 如果“最新 LTS”即 Java 21：已满足（pom 与 Dockerfile 已指向 21）。阻塞点：敏感配置与第三方库测试（低阻塞）。
2. 如果目标为 >21（未来 LTS）：主阻塞点为第三方库与框架（尤其与 Jakarta 命名空间相关的库）兼容性，需要逐个验证并替换不兼容库（中-高阻塞）。

**推荐的迁移步骤（高层次，3-6 步）**

1. 敏感信息处置（优先）：将 `application-prod.properties` 中的密码与密钥迁移到环境变量或 Vault，并从仓库移除或加密（风险：高）。
2. 确认目标 Java LTS：如果仍为 Java 21，则仅验证 CI/CD 与运行时；若目标 >21，先建立兼容清单（识别所有可能受影响的库）。
3. 在隔离的测试环境中构建并运行完整测试套件（单元 + 集成），修复编译或运行时错误。 
4. 升级/替换不兼容的第三方库（按优先级：数据库驱动、ORM/MyBatis、序列化/安全库等），并再次测试。 
5. 更新 Dockerfile 基础镜像（如需要），在容器中验证启动与健康检查；准备 Docker 部署方案供宝塔使用（或直接使用 Fat JAR + 系统服务）。
6. 上线前：创建并检查宝塔服务器目录与权限（`/www/wwwlogs/mc_server`），并准备启动脚本或 docker-compose 服务；逐步灰度发布并监控日志与性能。

**报告文件已保存路径**

- JSON：.github/modernize/assessment/report-20260526T080000/report.json
- Markdown：.github/modernize/assessment/report-20260526T080000/report.md

---
如需，我可以：

- 生成一个可执行的迁移检查表（逐依赖的兼容性检测清单），或
- 在 CI 中添加 Java 版本与依赖扫描（示例 GitHub Actions），或
- 帮你把 `application-prod.properties` 改为从环境变量读取并展示改动补丁（不在本次代理内直接修改源代码，需用户确认）。
