package com.gzasc.wechatappwaimai.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Shop {
    private Long id;
    private String name;           // 店铺名称
    private String logo;           // 店铺 logo URL
    private String phone;          // 联系电话
    private String description;    // 店铺描述
    private BigDecimal score;      // 评分（如 4.5）
    private Integer status;        // 状态：0-待审核，1-营业中，2-已歇业，3-审核拒绝
    private Long ownerUserId;      // 店主用户 ID（商家注册时绑定）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}