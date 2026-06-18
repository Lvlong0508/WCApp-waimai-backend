package com.gzasc.wechatappwaimai.dto;

import lombok.Data;

@Data
public class AddressRequest {
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
}
