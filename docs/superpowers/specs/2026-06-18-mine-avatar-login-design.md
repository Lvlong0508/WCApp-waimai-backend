# Mine 页面头像登录注册 设计文档

- 日期：2026-06-18
- 范围：小程序 `pages/mine` + 后端 `/api/user/login` + 新增 `/api/upload/temp-avatar`
- 状态：待实现

## 1. 背景与目标

当前 mine 页面用户信息卡片显示默认头像，未登录时有独立"登录"按钮，调用 `app.login()` 走静默 `wx.login` 流程；新用户在数据库里的昵称被写死为"微信用户"，头像字段为空。

目标：点击 mine 页面头像区域时弹出页内授权弹框，引导用户选择微信头像并输入/选择微信昵称，确认后完成首次注册或登录，并把昵称、头像写入用户数据。

## 2. 受限的技术约束

微信小程序自 2022-10 起回收了 `wx.getUserProfile`，无法一键直接拿到微信用户头像和昵称。官方合规方案：

- 头像：`<button open-type="chooseAvatar" bindchooseavatar>` 由用户主动选择微信头像
- 昵称：`<input type="nickname">` 在键盘上方提供微信昵称候选，也可手动输入

`chooseAvatar` 返回的 `avatarUrl` 是本地临时路径（`wxfile://tmp_xxx`），不能长期存储，必须先上传到后端换成可访问 URL。

## 3. 用户交互

### 未登录态

```
头像（默认图 + "点击登录注册"）
   │ bindtap onAvatarTap
   ▼
页内浮层授权弹框
  ┌────────────────────────────┐
  │  完善资料                  │
  │  [○ chooseAvatar 圆形头像] │
  │  [type=nickname 输入框]    │
  │  [取消]      [确认登录]    │
  └────────────────────────────┘
   │ 用户选头像+输昵称，点确认
   ▼
1) 上传头像到 /api/upload/temp-avatar 拿永久 URL
2) wx.login 拿 code
3) POST /api/user/login {code, nickName, avatarUrl}
4) 写 globalData/storage，setData 刷新 mine 页，关弹框
```

### 已登录态

本次不实现修改资料。已登录用户点头像不响应。

## 4. 前端改动

### 4.1 `pages/mine/mine.wxml`

- 头像区域 `bindtap="onAvatarTap"`
- 删除原独立"登录"按钮；未登录时头像下方文字显示 `点击登录注册`
- 新增弹框节点（`wx:if="{{showAuthModal}}"`）
  - 遮罩 `bindtap="closeAuthModal"`
  - 卡片 `catchtap` 阻止冒泡
    - 圆形头像按钮：`<button class="avatar-picker" open-type="chooseAvatar" bindchooseavatar="onChooseAvatar">` 内含 image
    - 昵称输入：`<input type="nickname" value="{{tempNickName}}" bindinput="onNickNameInput" placeholder="请输入昵称" maxlength="20">`
    - 操作按钮：取消 / 确认登录（`disabled="{{submitting}}"`）

### 4.2 `pages/mine/mine.wxss`

- 遮罩 `position:fixed; inset:0; background:rgba(0,0,0,.5); z-index:999`
- 卡片居中、白色圆角、内边距
- chooseAvatar 按钮去默认样式（`button::after { border: none; }` + `.avatar-picker { background: transparent; padding: 0; }`），尺寸 160rpx 圆形

### 4.3 `pages/mine/mine.js`

新增 data：

```js
data: {
  userInfo: {},
  defaultAddress: '',
  showAuthModal: false,
  tempAvatarUrl: '',
  tempNickName: '',
  submitting: false
}
```

新增/修改方法：

- `onAvatarTap`：若 `app.globalData.token` 存在直接 return；否则 setData 打开弹框
- `onChooseAvatar(e)`：保存 `e.detail.avatarUrl` 到 `tempAvatarUrl`
- `onNickNameInput(e)`：保存到 `tempNickName`
- `closeAuthModal`：重置临时态，关弹框
- `submitAuth`：
  1. 校验：任一为空 → toast
  2. `setData({ submitting: true })`
  3. `wx.uploadFile` 到 `/api/upload/temp-avatar` 拿 `url`
  4. `wx.login` 拿 `code`
  5. `app.loginWithProfile(code, tempNickName, url, cb)`
  6. 成功：setData userInfo + 关弹框 + 清空临时态 + `fetchDefaultAddress`
  7. 失败：toast + `submitting=false`
- 删除原 `handleLogin`

### 4.4 `app.js`

新增 `loginWithProfile(code, nickName, avatarUrl, callback)`：POST `/user/login` 时带上 `nickName` 和 `avatarUrl`，其他逻辑与原 `login` 相同。原 `login` 保留（用于 onLaunch checkSession 失败的静默场景）。

## 5. 后端改动

### 5.1 `dto/LoginRequest.java`

新增字段：

```java
private String code;
private String nickName;   // 可选，新增
private String avatarUrl;  // 可选，新增
```

### 5.2 `mapper/UserMapper.java`

新增方法：

```java
int updateProfile(@Param("id") Long id,
                  @Param("nickName") String nickName,
                  @Param("avatarUrl") String avatarUrl);
```

### 5.3 `mapper/UserMapper.xml`

新增动态 update（仅更新非空字段）：

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

### 5.4 `service/UserService.java`

改签名：`Map<String, Object> login(LoginRequest req);`

### 5.5 `service/impl/UserServiceImpl.java`

`login(LoginRequest req)` 逻辑：

1. 校验 req 与 req.code
2. `wxApiUtil.jscode2session(code)` 拿 openid、unionid
3. 查 user
4. 若新用户：nickName 取 req.nickName（空则 "微信用户"），avatarUrl 取 req.avatarUrl，insert
5. 若老用户：req.nickName 或 req.avatarUrl 任一非空 → `updateProfile`，再 `selectById` 拿最新
6. `StpUtil.login(user.getId())`，返回 `{token, accountType, userInfo}`

### 5.6 `controller/user/UserAuthController.java`

`userService.login(request.getCode())` 改成 `userService.login(request)`，参数校验逻辑不变。

### 5.7 新增 `controller/PublicFileController.java`

```java
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

此接口不要求登录；若项目使用了 sa-token 全局拦截器，需要把该路径加入白名单（需在实现时确认拦截器是否存在）。

### 5.8 `service/FileService.java` 与 `FileServiceImpl.java`

新增接口：`String uploadTempAvatar(MultipartFile file);`

实现要点：

- 复用 `validateFile`（png/jpg、≤5MB）
- 存到 `uploadPath/temp_avatar/<yyyyMMdd>/<random>.<ext>`
- 不写 `upload_file` 表（避免 userId 必填问题，属临时资源）
- 返回 `accessUrl + "/image/temp_avatar/<yyyyMMdd>/<random>.<ext>"`

## 6. 数据流时序

```
mine.js     /api/upload/temp-avatar    wx.login     /api/user/login
   |               |                       |               |
   |-- uploadFile->|                       |               |
   |<-- { url } ---|                       |               |
   |-- wx.login() -----------------------> |               |
   |<-- { code } ----------------------------              |
   |-- POST {code, nickName, avatarUrl} ------------------>|
   |<-- { token, userInfo } -------------------------------|
   | setData / 写 storage / 关弹框
```

## 7. 边界与异常

- 用户取消 chooseAvatar：tempAvatarUrl 维持原值（不报错）
- 未选头像就点确认：toast "请选择头像"
- 未填昵称就点确认：toast "请输入昵称"
- 头像上传失败：toast "头像上传失败"，submitting=false，弹框保留
- wx.login 失败：toast "微信登录失败"
- 登录接口失败：toast 后端 msg 或 "登录失败"，弹框保留
- 文件类型不合规：后端 400，前端 toast

## 8. 不在本次范围

- 已登录用户修改头像/昵称
- temp_avatar 目录的过期清理（作业项目暂不做）
- 接口对老调用方（原 app.login 静默登录）的兼容已通过"字段可选"保证

## 9. 验证清单

- [ ] 未登录点头像 → 弹框出现
- [ ] chooseAvatar 选完头像 → 弹框圆形头像更新
- [ ] 输昵称 → tempNickName 同步
- [ ] 点确认 → 后端 user 表插入了新用户，nick_name 和 avatar_url 非空
- [ ] mine 页头像和昵称替换成用户选择的内容
- [ ] 卸载小程序后重新打开仍能保持登录态（token 持久化）
- [ ] 同一账号第二次走流程：user 表 update 而非 insert
