package com.gzasc.wechatappwaimai.vo;

import lombok.Data;

@Data
public class AddressVO {
    private Long id;
    private String name;           // 收货人
    private String phone;          // 手机号
    private String province;       // 省
    private String city;           // 市
    private String district;       // 区
    private String detail;         // 详细地址
    private Integer isDefault;     // 是否默认 0-否 1-是
}