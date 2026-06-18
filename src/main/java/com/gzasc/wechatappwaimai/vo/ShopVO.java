package com.gzasc.wechatappwaimai.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopVO {
    private Long id;
    private String name;           // 店铺名称
    private String logo;           // 店铺 logo URL
    private String phone;          // 联系电话
    private String description;    // 店铺描述
    private BigDecimal score;      // 评分（如 4.5）
    private Integer status;        // 状态：0-待审核，1-营业中，2-已歇业，3-审核拒绝
}