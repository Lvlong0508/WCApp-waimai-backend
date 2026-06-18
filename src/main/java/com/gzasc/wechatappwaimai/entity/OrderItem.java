package com.gzasc.wechatappwaimai.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;          // 订单 ID
    private Long productId;        // 商品 ID
    private String productName;    // 商品快照：名称
    private String productImage;   // 商品快照：图片
    private BigDecimal price;      // 商品快照：下单时单价
    private Integer quantity;      // 数量
}