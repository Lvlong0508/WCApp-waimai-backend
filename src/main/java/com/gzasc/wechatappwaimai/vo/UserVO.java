package com.gzasc.wechatappwaimai.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Long id;
    private String nickName;
    private String avatarUrl;
    private String phone;       // 可空
    private String accountType; // 可选：USER / MERCHANT
}