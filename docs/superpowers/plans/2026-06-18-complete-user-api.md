# Complete User API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现现有 5 个用户端 Controller 注释中的全部接口，并补齐 Mapper、Service、SQL。

**Architecture:** 采用 Controller -> Service -> Mapper -> MySQL。Controller 负责鉴权和请求响应，Service 负责业务规则，Mapper/XML 负责 SQL。登录使用开发模拟登录：code 作为 openid，不存在则创建用户。

**Tech Stack:** Spring Boot 3, MyBatis, Sa-Token, MySQL, Lombok, Hutool

---

### Task 1: DTO 与 VO 修正

**Files:**
- Create: `src/main/java/com/gzasc/wechatappwaimai/dto/LoginRequest.java`
- Create: `src/main/java/com/gzasc/wechatappwaimai/dto/ShopRegisterRequest.java`
- Create: `src/main/java/com/gzasc/wechatappwaimai/dto/AddressRequest.java`
- Modify: `src/main/java/com/gzasc/wechatappwaimai/vo/OrderVO.java`

- [ ] 创建登录、商家注册、地址请求 DTO。
- [ ] 修正 `OrderVO.shopName` 类型为 `String`，`orderItemList` 字段用于返回订单明细。

### Task 2: Mapper 层补齐

**Files:**
- Create: `UserMapper.java/xml`
- Create: `ShopMapper.java/xml`
- Create: `AddressMapper.java/xml`
- Create: `OrderMapper.java/xml`
- Modify/Create: `FileMapper.java/xml`

- [ ] UserMapper：按 openid 查询、按 id 查询、新增用户。
- [ ] ShopMapper：分页查询营业中店铺、新增店铺。
- [ ] AddressMapper：查默认、查列表、按用户和 id 查、增删改、清空默认。
- [ ] OrderMapper：查当前用户订单、查订单明细。
- [ ] FileMapper：保留上传记录 CRUD，包含 `deleteByEntity`。

### Task 3: Service 层补齐

**Files:**
- Create: `UserService.java/impl/UserServiceImpl.java`
- Create: `ShopService.java/impl/ShopServiceImpl.java`
- Create: `AddressService.java/impl/AddressServiceImpl.java`
- Create: `OrderService.java/impl/OrderServiceImpl.java`
- Modify: `FileService.java/impl/FileServiceImpl.java`

- [ ] UserService：开发模拟登录和当前用户信息。
- [ ] ShopService：店铺列表、商家注册。
- [ ] AddressService：当前用户地址增删改查，默认地址互斥。
- [ ] OrderService：当前用户订单分页列表，包含明细。
- [ ] FileService：头像和店铺 logo 替换旧图，商品图允许多张。

### Task 4: Controller 实现

**Files:**
- Modify: `UserAuthController.java`
- Modify: `UserShopController.java`
- Modify: `UserAddressController.java`
- Modify: `UserOrderController.java`
- Modify: `UserFileController.java`

- [ ] Auth：`POST /api/user/login`、`GET /api/user/info`。
- [ ] Shop：`GET /api/shops`、`POST /api/merchant/register`。
- [ ] Address：默认、列表、详情、新增、更新、删除。
- [ ] Order：`GET /api/orders`。
- [ ] File：用户头像、商品图片、店铺 logo 上传。

### Task 5: SQL 与配置

**Files:**
- Modify: `sql/file.sql`
- Modify: `src/main/resources/application.yml`
- Modify/Create: `src/main/java/com/gzasc/wechatappwaimai/config/WebMvcConfig.java`

- [ ] SQL 创建 `user、shop、address、product、order、order_item、file` 表。
- [ ] application.yml 配置数据源、MyBatis、文件上传路径。
- [ ] WebMvcConfig 使用 `file.upload.path` 映射 `/image/**`。

### Task 6: 验证

- [ ] 运行 `mvn test`。
- [ ] 若 JAVA_HOME 未配置，记录阻塞原因。
- [ ] 修复编译错误后再次验证。
