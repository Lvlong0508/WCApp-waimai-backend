package com.gzasc.wechatappwaimai.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;        // 订单号（规则生成，如时间戳+随机数）
    private String shopName;         // 店铺 Name
    private BigDecimal totalPrice; // 实付总金额
    private Integer status;        // 订单状态：0-待支付，1-制作中，2-已完成，3-已取消
    private String remark;         // 备注
    private LocalDateTime createTime;
    private List<OrderItemVO> orderItemList;
}