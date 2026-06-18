# Mine 页面头像登录注册 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** mine 页面点击头像弹出页内授权框，让用户通过 chooseAvatar + nickname 完成首次微信注册或登录，并把昵称、头像写入数据库。

**Architecture:** 前端在 mine 页内增加授权弹层（小程序合规的 chooseAvatar + type=nickname 组合），分两步调用后端：先调用新增的免登录 `/api/upload/temp-avatar` 把本地临时头像上传换成 URL，再调 `wx.login` 拿 code 后调用扩展后的 `/api/user/login` 提交 `{code, nickName, avatarUrl}`；后端在 login 接口中对新用户写入、老用户更新 user 表的昵称和头像。

**Tech Stack:** 微信小程序 (WXML/WXSS/JS) + Spring Boot 3 + MyBatis + sa-token + Hutool

**说明：** 本项目无单元测试基础设施（后端无 JUnit 测试目录，前端无小程序测试框架），无法严格 TDD。采用 "编译验证 + 端到端手动验证 + 微信开发者工具调试 + 数据库检查" 方式确认每个任务正确性。每完成一个有意义的功能切片即提交一次。

---

## 文件结构

**后端新建：**
- `src/main/java/com/gzasc/wechatappwaimai/controller/PublicFileController.java`

**后端修改：**
- `src/main/java/com/gzasc/wechatappwaimai/dto/LoginRequest.java`
- `src/main/java/com/gzasc/wechatappwaimai/mapper/UserMapper.java`
- `src/main/resources/mapper/UserMapper.xml`
- `src/main/java/com/gzasc/wechatappwaimai/service/UserService.java`
- `src/main/java/com/gzasc/wechatappwaimai/service/impl/UserServiceImpl.java`
- `src/main/java/com/gzasc/wechatappwaimai/controller/user/UserAuthController.java`
- `src/main/java/com/gzasc/wechatappwaimai/service/FileService.java`
- `src/main/java/com/gzasc/wechatappwaimai/service/impl/FileServiceImpl.java`

**前端修改（路径基址 `F:\大三\大三下\前端应用\大作业\外卖点单`）：**
- `app.js`
- `pages/mine/mine.wxml`
- `pages/mine/mine.wxss`
- `pages/mine/mine.js`

---

## Task 1：后端 LoginRequest DTO 扩展

**Files:**
- Modify: `src/main/java/com/gzasc/wechatappwaimai/dto/LoginRequest.java`

- [ ] **Step 1：将文件完整替换为以下内容**

```java
package com.gzasc.wechatappwaimai.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;
    private String nickName;
    private String avatarUrl;
}
```

- [ ] **Step 2：编译验证**

Run: `mvn compile -q` （工作目录 `F:\IdeaProjects\WeChatApp-waimai`）
Expected: BUILD SUCCESS。

- [ ] **Step 3：提交**

```bash
git add src/main/java/com/gzasc/wechatappwaimai/dto/LoginRequest.java
git commit -m "feat(user): LoginRequest 增加 nickName 和 avatarUrl"
```

---

## Task 2：UserMapper 增加 updateProfile

**Files:**
- Modify: `src/main/java/com/gzasc/wechatappwaimai/mapper/UserMapper.java`
- Modify: `src/main/resources/mapper/UserMapper.xml`

- [ ] **Step 1：UserMapper.java 在 `int insert(User user);` 之前插入新方法**

```java
    int updateProfile(@Param("id") Long id,
                      @Param("nickName") String nickName,
                      @Param("avatarUrl") String avatarUrl);

```

- [ ] **Step 2：UserMapper.xml 在 `</mapper>` 之前追加**

```xml
    <update id="updateProfile">
        UPDATE user
        <set>
            <if test="nickName != null and nickName != ''">nick_name = #{nickName},</if>
            <if test="avatarUrl != null and avatarUrl != ''">avatar_url = #{avatarUrl},</if>
            update_time = NOW()
        </set>
        WHERE id = #{id}
    </update>

```

- [ ] **Step 3：编译验证**

Run: `mvn compile -q`
Expected: BUILD SUCCESS。

- [ ] **Step 4：提交**

```bash
git add src/main/java/com/gzasc/wechatappwaimai/mapper/UserMapper.java src/main/resources/mapper/UserMapper.xml
git commit -m "feat(user): UserMapper 增加 updateProfile 动态更新"
```

---

## Task 3：UserService 改签名 + UserServiceImpl 重写 login + Controller 调用更新

**Files:**
- Modify: `src/main/java/com/gzasc/wechatappwaimai/service/UserService.java`
- Modify: `src/main/java/com/gzasc/wechatappwaimai/service/impl/UserServiceImpl.java`
- Modify: `src/main/java/com/gzasc/wechatappwaimai/controller/user/UserAuthController.java`

- [ ] **Step 1：修改 UserService 接口**

把 `UserService.java` 中 `Map<String, Object> login(String code);` 改为 `Map<String, Object> login(LoginRequest req);`，并 import：

```java
import com.gzasc.wechatappwaimai.dto.LoginRequest;
```

- [ ] **Step 2：重写 UserServiceImpl.login**

在 `UserServiceImpl.java` 的 import 区追加 `import com.gzasc.wechatappwaimai.dto.LoginRequest;`，然后把原 `login(String code)` 方法整段（第 23-54 行）替换为：

```java
    @Override
    public Map<String, Object> login(LoginRequest req) {
        if (req == null || req.getCode() == null || req.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("code不能为空");
        }
        String code = req.getCode().trim();

        Map<String, Object> wxResult = wxApiUtil.jscode2session(code);
        String openid = (String) wxResult.get("openid");
        String unionid = (String) wxResult.get("unionid");
        if (openid == null || openid.trim().isEmpty()) {
            throw new IllegalArgumentException("微信登录失败: openid为空");
        }

        String reqNickName = req.getNickName();
        String reqAvatarUrl = req.getAvatarUrl();
        boolean hasNick = reqNickName != null && !reqNickName.trim().isEmpty();
        boolean hasAvatar = reqAvatarUrl != null && !reqAvatarUrl.trim().isEmpty();

        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            LocalDateTime now = LocalDateTime.now();
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setNickName(hasNick ? reqNickName.trim() : "微信用户");
            user.setAvatarUrl(hasAvatar ? reqAvatarUrl.trim() : null);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            userMapper.insert(user);
        } else if (hasNick || hasAvatar) {
            userMapper.updateProfile(user.getId(),
                    hasNick ? reqNickName.trim() : null,
                    hasAvatar ? reqAvatarUrl.trim() : null);
            user = userMapper.selectById(user.getId());
        }

        StpUtil.login(user.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        result.put("accountType", "USER");
        result.put("userInfo", toUserVO(user));
        return result;
    }
```

- [ ] **Step 3：修改 UserAuthController 调用**

`UserAuthController.java` 第 29 行 `Map<String, Object> data = userService.login(request.getCode());` 改为：

```java
        Map<String, Object> data = userService.login(request);
```

- [ ] **Step 4：编译验证**

Run: `mvn compile -q`
Expected: BUILD SUCCESS。

- [ ] **Step 5：提交**

```bash
git add src/main/java/com/gzasc/wechatappwaimai/service/UserService.java src/main/java/com/gzasc/wechatappwaimai/service/impl/UserServiceImpl.java src/main/java/com/gzasc/wechatappwaimai/controller/user/UserAuthController.java
git commit -m "feat(user): /user/login 支持携带 nickName 和 avatarUrl 注册或更新"
```

---


## Task 4：FileService 增加 uploadTempAvatar 方法

**Files:**
- Modify: `src/main/java/com/gzasc/wechatappwaimai/service/FileService.java`
- Modify: `src/main/java/com/gzasc/wechatappwaimai/service/impl/FileServiceImpl.java`

- [ ] **Step 1：FileService 接口末尾追加方法**

在 `FileService.java` 末尾 `}` 之前插入：

```java

    /**
     * 上传未登录用户的临时头像（按日期分目录，不写 upload_file 表）
     * @param file 图片文件
     * @return 可访问的完整URL
     */
    String uploadTempAvatar(MultipartFile file);
```

- [ ] **Step 2：FileServiceImpl 追加 import 和实现**

在 `FileServiceImpl.java` 的 import 区追加：

```java
import java.time.format.DateTimeFormatter;
```

在类末尾 `}` 之前插入：

```java

    @Override
    public String uploadTempAvatar(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        String extension = FileUtil.extName(originalFilename).toLowerCase();
        if (!Arrays.asList("png", "jpg", "jpeg").contains(extension)) {
            throw new IllegalArgumentException("只支持 png/jpg 格式的图片");
        }

        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String relativeDir = "temp_avatar/" + datePart;
        File dir = new File(uploadPath, relativeDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + randomLetters() + "." + extension;
        File destFile = new File(dir, fileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败: " + e.getMessage());
        }

        return accessUrl + "/image/" + relativeDir + "/" + fileName;
    }
```

- [ ] **Step 3：编译验证**

Run: `mvn compile -q`
Expected: BUILD SUCCESS。

- [ ] **Step 4：提交**

```bash
git add src/main/java/com/gzasc/wechatappwaimai/service/FileService.java src/main/java/com/gzasc/wechatappwaimai/service/impl/FileServiceImpl.java
git commit -m "feat(file): FileService 增加免登录临时头像上传方法"
```

---

## Task 5：新增 PublicFileController（免登录上传接口）

**Files:**
- Create: `src/main/java/com/gzasc/wechatappwaimai/controller/PublicFileController.java`

- [ ] **Step 1：创建文件**

写入以下内容：

```java
package com.gzasc.wechatappwaimai.controller;

import com.gzasc.wechatappwaimai.dto.WcwmResponse;
import com.gzasc.wechatappwaimai.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicFileController {

    private final FileService fileService;

    @PostMapping("/upload/temp-avatar")
    public WcwmResponse<Map<String, String>> uploadTempAvatar(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadTempAvatar(file);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return WcwmResponse.success(data);
    }
}
```

- [ ] **Step 2：确认 sa-token 拦截器配置**

Run（工作目录 `F:\IdeaProjects\WeChatApp-waimai`）：

```powershell
Get-ChildItem -Recurse -Path src\main\java -File | Select-String -Pattern "SaInterceptor|SaServletFilter|StpInterceptor" | Select-Object Path,LineNumber,Line
```

Expected: 当前项目无输出 → 没有全局 sa-token 拦截器，只有各 Controller 手动 `StpUtil.checkLogin()`。本步无需改动；新建的 PublicFileController 不调用 `checkLogin`，自然免登录。
若有输出则需在对应配置类的白名单中加入 `/api/upload/temp-avatar` 和 `/api/user/login`。

- [ ] **Step 3：编译验证**

Run: `mvn compile -q`
Expected: BUILD SUCCESS。

- [ ] **Step 4：端到端验证**

启动后端（保持运行）：

```bash
mvn spring-boot:run
```

新开终端：

```powershell
$form = @{ file = Get-Item 'F:\大三\大三下\前端应用\大作业\外卖点单\images\mine\default.png' }
Invoke-RestMethod -Uri 'http://localhost:8080/api/upload/temp-avatar' -Method Post -Form $form
```

Expected: 返回 JSON 类似 `code=200, data.url='http://localhost:8080/image/temp_avatar/20260618/xxx.png'`。浏览器直接访问该 url 能看到图片。
若 default.png 不存在，可换任意一张本地 png/jpg 测试。

- [ ] **Step 5：提交**

```bash
git add src/main/java/com/gzasc/wechatappwaimai/controller/PublicFileController.java
git commit -m "feat(file): 新增免登录 /api/upload/temp-avatar 接口"
```

---

## Task 6：前端 app.js 增加 loginWithProfile 方法

**Files:**
- Modify: `app.js`（路径 `F:\大三\大三下\前端应用\大作业\外卖点单\app.js`）

- [ ] **Step 1：在原 login 方法之前插入新方法**

在 `app.js` 找到第 22 行 `login(callback) {`（注释 `// 微信登录...` 一行）之前，插入下面一段（保持 App({...}) 内方法间逗号语法正确，本方法以 `,` 结尾）：

```javascript
  // 带头像和昵称登录（首次注册或更新资料场景）
  loginWithProfile(code, nickName, avatarUrl, callback) {
    wx.request({
      url: `${this.globalData.baseUrl}/user/login`,
      method: 'POST',
      data: { code, nickName, avatarUrl },
      success: (response) => {
        const data = response.data;
        if (data.code === 200) {
          this.globalData.token = data.data.token;
          this.globalData.userInfo = data.data.userInfo;
          wx.setStorageSync('token', data.data.token);
          callback && callback(true);
        } else {
          wx.showToast({ title: data.msg || '登录失败', icon: 'none' });
          callback && callback(false);
        }
      },
      fail: () => {
        wx.showToast({ title: '网络异常', icon: 'none' });
        callback && callback(false);
      }
    });
  },

```

- [ ] **Step 2：微信开发者工具编译预览**

在微信开发者工具点「编译」，确认调试控制台无 JS 报错。

- [ ] **Step 3：提交（前端目录）**

```bash
git add app.js
git commit -m "feat(app): 新增 loginWithProfile 方法以提交昵称与头像"
```

若前端目录无 git，请按现有提交方式处理。

---

## Task 7：前端 mine 页面 WXML + WXSS（弹框结构与样式）

**Files:**
- Modify: `pages/mine/mine.wxml`
- Modify: `pages/mine/mine.wxss`

- [ ] **Step 1：将 mine.wxml 完整替换为以下内容**

```xml
<!--pages/mine/mine.wxml-->
<view class="mine-container">
  <!-- 用户信息卡片 -->
  <view class="user-card" bindtap="onAvatarTap">
    <image class="avatar" src="{{userInfo.avatarUrl || '/images/mine/default.png'}}" mode="aspectFill" />
    <view class="user-name">{{userInfo.nickName || '点击登录注册'}}</view>
  </view>

  <!-- 收货地址 -->
  <view class="menu-card">
    <view class="menu-item" bindtap="goToAddress">
      <text class="menu-icon">📍</text>
      <text class="menu-title">收货地址</text>
      <text class="menu-desc">{{defaultAddress || '未设置'}}</text>
      <text class="arrow">></text>
    </view>
  </view>

  <!-- 商家入口 -->
  <view class="menu-card">
    <view class="menu-item" bindtap="goToMerchantRegister">
      <text class="menu-icon">🏪</text>
      <text class="menu-title">商家注册</text>
      <text class="arrow">></text>
    </view>
  </view>

  <!-- 授权登录弹框 -->
  <view class="auth-mask" wx:if="{{showAuthModal}}" bindtap="closeAuthModal" catchtouchmove="noop">
    <view class="auth-card" catchtap="noop">
      <view class="auth-title">完善资料并登录</view>

      <button class="avatar-picker"
              open-type="chooseAvatar"
              bindchooseavatar="onChooseAvatar">
        <image class="avatar-picker-img"
               src="{{tempAvatarUrl || '/images/mine/default.png'}}"
               mode="aspectFill" />
      </button>
      <view class="auth-tip">点击上方圆形头像选择微信头像</view>

      <input class="nickname-input"
             type="nickname"
             value="{{tempNickName}}"
             bindinput="onNickNameInput"
             placeholder="请输入昵称"
             maxlength="20" />

      <view class="auth-actions">
        <button class="auth-btn cancel" bindtap="closeAuthModal" disabled="{{submitting}}">取消</button>
        <button class="auth-btn confirm" bindtap="submitAuth" disabled="{{submitting}}" loading="{{submitting}}">确认登录</button>
      </view>
    </view>
  </view>
</view>
```

- [ ] **Step 2：在 mine.wxss 末尾追加弹框样式**

```css

/* 授权弹框样式 */
.auth-mask {
  position: fixed;
  top: 0; right: 0; bottom: 0; left: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}
.auth-card {
  width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx 32rpx 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.auth-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 32rpx;
}
.avatar-picker {
  width: 160rpx;
  height: 160rpx;
  padding: 0;
  margin: 0 0 12rpx 0;
  border-radius: 50%;
  background: #f5f5f5;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: normal;
}
.avatar-picker::after {
  border: none;
}
.avatar-picker-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  display: block;
}
.auth-tip {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 24rpx;
}
.nickname-input {
  width: 100%;
  height: 80rpx;
  padding: 0 24rpx;
  border: 1rpx solid #e5e5e5;
  border-radius: 12rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  margin-bottom: 32rpx;
}
.auth-actions {
  width: 100%;
  display: flex;
  justify-content: space-between;
}
.auth-btn {
  width: 240rpx;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  border-radius: 40rpx;
  padding: 0;
}
.auth-btn.cancel {
  background: #f5f5f5;
  color: #666;
}
.auth-btn.confirm {
  background: #ff6b00;
  color: #fff;
}
```

- [ ] **Step 3：微信开发者工具编译预览**

点「编译」，未登录时 mine 页面看到「点击登录注册」文字；目前点击仍无反应（逻辑在 Task 8 中实现）。

- [ ] **Step 4：提交**

```bash
git add pages/mine/mine.wxml pages/mine/mine.wxss
git commit -m "feat(mine): mine 页头像区可点击 + 授权弹框结构与样式"
```

---

## Task 8：前端 mine.js 实现授权弹框逻辑

**Files:**
- Modify: `pages/mine/mine.js`

- [ ] **Step 1：将 mine.js 完整替换为以下内容**

```javascript
// pages/mine/mine.js
const app = getApp();
const { request } = require('../../utils/request');

Page({
  data: {
    userInfo: {},
    defaultAddress: '',
    showAuthModal: false,
    tempAvatarUrl: '',
    tempNickName: '',
    submitting: false
  },

  onShow() {
    this.setData({ userInfo: app.globalData.userInfo || {} });
    if (app.globalData.token) {
      this.fetchDefaultAddress();
    }
  },

  // 空函数，用于阻止冒泡
  noop() {},

  // 点击头像区
  onAvatarTap() {
    if (app.globalData.token) {
      return;
    }
    this.setData({
      showAuthModal: true,
      tempAvatarUrl: '',
      tempNickName: ''
    });
  },

  // 微信选择头像回调
  onChooseAvatar(e) {
    this.setData({ tempAvatarUrl: e.detail.avatarUrl });
  },

  // 昵称输入
  onNickNameInput(e) {
    this.setData({ tempNickName: e.detail.value });
  },

  // 关闭弹框
  closeAuthModal() {
    if (this.data.submitting) return;
    this.setData({
      showAuthModal: false,
      tempAvatarUrl: '',
      tempNickName: ''
    });
  },

  // 提交授权并登录
  submitAuth() {
    const { tempAvatarUrl, tempNickName } = this.data;
    if (!tempAvatarUrl) {
      wx.showToast({ title: '请选择头像', icon: 'none' });
      return;
    }
    const nick = (tempNickName || '').trim();
    if (!nick) {
      wx.showToast({ title: '请输入昵称', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });

    // 步骤1：上传头像换 URL
    wx.uploadFile({
      url: `${app.globalData.baseUrl}/upload/temp-avatar`,
      filePath: tempAvatarUrl,
      name: 'file',
      success: (uploadRes) => {
        let avatarUrl = '';
        try {
          const parsed = JSON.parse(uploadRes.data);
          if (parsed.code === 200 && parsed.data && parsed.data.url) {
            avatarUrl = parsed.data.url;
          } else {
            wx.showToast({ title: parsed.msg || '头像上传失败', icon: 'none' });
            this.setData({ submitting: false });
            return;
          }
        } catch (err) {
          wx.showToast({ title: '头像上传响应异常', icon: 'none' });
          this.setData({ submitting: false });
          return;
        }

        // 步骤2：wx.login 拿 code
        wx.login({
          success: (loginRes) => {
            if (!loginRes.code) {
              wx.showToast({ title: '微信登录失败', icon: 'none' });
              this.setData({ submitting: false });
              return;
            }
            // 步骤3：调登录接口
            app.loginWithProfile(loginRes.code, nick, avatarUrl, (ok) => {
              if (ok) {
                this.setData({
                  userInfo: app.globalData.userInfo,
                  showAuthModal: false,
                  submitting: false,
                  tempAvatarUrl: '',
                  tempNickName: ''
                });
                wx.showToast({ title: '登录成功', icon: 'success' });
                this.fetchDefaultAddress();
              } else {
                this.setData({ submitting: false });
              }
            });
          },
          fail: () => {
            wx.showToast({ title: '微信登录失败', icon: 'none' });
            this.setData({ submitting: false });
          }
        });
      },
      fail: () => {
        wx.showToast({ title: '头像上传失败', icon: 'none' });
        this.setData({ submitting: false });
      }
    });
  },

  // 【API】获取默认地址
  fetchDefaultAddress() {
    request('/address/default', 'GET')
      .then(data => {
        const addr = data;
        const addrStr = addr ? `${addr.province}${addr.city}${addr.district} ${addr.detail}` : '';
        this.setData({ defaultAddress: addrStr });
      })
      .catch(() => {});
  },

  // 跳转地址管理
  goToAddress() {
    wx.navigateTo({ url: '/pages/address/address' });
  },

  // 跳转商家注册
  goToMerchantRegister() {
    if (!app.globalData.token) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    wx.navigateTo({ url: '/pages/merchant-register/merchant-register' });
  },

  onLoad(options) {},
  onReady() {},
  onHide() {},
  onUnload() {},
  onPullDownRefresh() {},
  onReachBottom() {},
  onShareAppMessage() {}
});
```

- [ ] **Step 2：微信开发者工具编译预览**

确认调试控制台无 JS 报错。

- [ ] **Step 3：提交**

```bash
git add pages/mine/mine.js
git commit -m "feat(mine): 实现头像授权弹框的注册/登录逻辑"
```

---

## Task 9：端到端联调验证

**前置：**
- 后端跑起来：在 `F:\IdeaProjects\WeChatApp-waimai` 执行 `mvn spring-boot:run`
- 数据库 MySQL 启动，连接信息见 `application.yml`
- 微信开发者工具打开 `F:\大三\大三下\前端应用\大作业\外卖点单`
- 项目配置里勾选「不校验合法域名、HTTPS、TLS 版本及 HTTPS 证书」（开发期）

- [ ] **Step 1：测试新用户注册场景**

1. 数据库执行 `DELETE FROM user WHERE openid = '<你的测试 openid>';` 确保是新用户（或先把 user 表清空）
2. 在小程序中清缓存：开发者工具菜单 → 工具 → 清缓存 → 全部清除
3. 进入 mine 页，点击头像区域 → 弹出授权弹框
4. 点圆形头像 → 微信弹出头像选择 → 选完后圆形头像更新
5. 在昵称输入框输入或选择微信昵称
6. 点「确认登录」
7. 期望：toast「登录成功」，弹框关闭，mine 页头像和昵称变为刚刚选择的内容

- [ ] **Step 2：检查后端数据**

```sql
SELECT id, openid, nick_name, avatar_url, create_time FROM user ORDER BY id DESC LIMIT 1;
```
Expected: 新插入一条记录，`nick_name` 和 `avatar_url` 均非空，且 `avatar_url` 以 `http://localhost:8080/image/temp_avatar/...` 开头。

在浏览器直接访问 `avatar_url` 应能看到刚刚选的头像图片。

- [ ] **Step 3：测试老用户更新场景**

1. 不清缓存、不删数据库记录的情况下，重新进入 mine 页（已登录态）
2. 点头像应无任何反应（已登录不响应）
3. 清缓存后重进，再走一次流程并故意选不同头像、输入不同昵称
4. 数据库验证：同一条 user 记录，`nick_name` 和 `avatar_url` 被更新（且 `update_time` 已变化）

- [ ] **Step 4：测试异常场景**

| 场景 | 操作 | 预期 |
|------|------|------|
| 未选头像就提交 | 进入弹框直接输入昵称 → 确认 | toast「请选择头像」 |
| 未填昵称就提交 | 进入弹框只选头像 → 确认 | toast「请输入昵称」 |
| 关弹框 | 选好头像后点遮罩或取消 | 弹框消失，临时态清空，再点弹出时显示默认头像 |

- [ ] **Step 5：登录态持久化**

1. 完整登录一次
2. 微信开发者工具点「编译」重启小程序
3. 进入 mine 页：期望直接看到上次的头像和昵称（onLaunch 读 token，onShow 读 globalData.userInfo）

> 注意：项目当前 `app.onLaunch` 在 token 存在时仅刷新 session，没有同步 `getUserInfo`。若该步骤显示空昵称，可在 `app.js` 中 `getUserInfo` 成功回调里同步把 `globalData.userInfo` 写到 storage 后续读取；本次范围内可以接受首次重启 mine 页头像/昵称需 `getUserInfo` 异步返回后再 onShow 一次才显示。

- [ ] **Step 6：（无需提交）记录验证结果**

如果有 bug，回到对应 Task 修复并重新验证。

---

## 完成后清单

- [ ] 所有 Task 已完成且已提交
- [ ] 数据库验证通过
- [ ] mine 页未登录态正确弹框，已登录态正确显示头像和昵称
- [ ] 后端日志无异常堆栈
