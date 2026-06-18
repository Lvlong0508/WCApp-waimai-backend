package com.gzasc.wechatappwaimai.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Address {
    private Long id;
    private Long userId;           // 关联用户
    private String name;           // 收货人
    private String phone;          // 手机号
    private String province;       // 省
    private String city;           // 市
    private String district;       // 区
    private String detail;         // 详细地址
    private Integer isDefault;     // 是否默认 0-否 1-是
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}