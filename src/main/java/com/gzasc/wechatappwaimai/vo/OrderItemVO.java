package com.gzasc.wechatappwaimai.vo;

import lombok.Data;

@Data
public class OrderItemVO {
    private Long productId;        // 商品 ID
    private String productName;    // 商品快照：名称
    private Integer quantity;      // 数量
}