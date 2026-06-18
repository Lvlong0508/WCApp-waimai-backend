package com.gzasc.wechatappwaimai.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;                // 主键
    private String openid;          // 微信 openid，唯一索引
    private String unionid;         // 微信 unionid（可选）
    private String nickName;        // 用户昵称
    private String avatarUrl;       // 头像 URL
    private String phone;           // 手机号（可选，若需要绑定）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}