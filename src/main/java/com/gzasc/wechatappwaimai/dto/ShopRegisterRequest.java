package com.gzasc.wechatappwaimai.dto;

import lombok.Data;

@Data
public class ShopRegisterRequest {
    private String shopName;
    private String logo;
    private String phone;
    private String description;
}
