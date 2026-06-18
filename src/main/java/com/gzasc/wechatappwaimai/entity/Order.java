package com.gzasc.wechatappwaimai.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String orderNo;        // 订单号（规则生成，如时间戳+随机数）
    private Long userId;           // 下单用户
    private Long shopId;           // 店铺 ID
    private BigDecimal totalPrice; // 实付总金额
    private Integer status;        // 订单状态：0-待支付，1-制作中，2-已完成，3-已取消
    private String remark;         // 备注
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 以下字段可按需补充
    // private LocalDateTime payTime;
    // private Integer payType;    // 支付方式
}