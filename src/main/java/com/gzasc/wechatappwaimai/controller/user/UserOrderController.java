package com.gzasc.wechatappwaimai.controller.user;

import cn.dev33.satoken.stp.StpUtil;
import com.gzasc.wechatappwaimai.dto.WcwmResponse;
import com.gzasc.wechatappwaimai.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserOrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public WcwmResponse<Map<String, Object>> listOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        Map<String, Object> data = orderService.listOrders(userId, page, size);
        log.info("用户查询订单列表 userId={}, page={}", userId, page);
        return WcwmResponse.success(data);
    }
}
