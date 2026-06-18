package com.gzasc.wechatappwaimai.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private Long shopId;           // 所属店铺
    private String name;           // 商品名称
    private String image;          // 图片 URL
    private BigDecimal price;      // 单价
    private String description;    // 描述
    private Integer status;        // 状态：0-下架，1-上架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}