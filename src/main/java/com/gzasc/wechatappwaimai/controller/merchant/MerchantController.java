package com.gzasc.wechatappwaimai.controller.merchant;

import cn.dev33.satoken.stp.StpUtil;
import com.gzasc.wechatappwaimai.dto.WcwmResponse;
import com.gzasc.wechatappwaimai.entity.Product;
import com.gzasc.wechatappwaimai.entity.Shop;
import com.gzasc.wechatappwaimai.mapper.ProductMapper;
import com.gzasc.wechatappwaimai.mapper.ShopMapper;
import com.gzasc.wechatappwaimai.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
@Slf4j
public class MerchantController {

    private final ShopMapper shopMapper;
    private final ProductMapper productMapper;
    private final OrderService orderService;

    @GetMapping("/goods")
    public WcwmResponse<List<Product>> listGoods() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        Shop shop = shopMapper.selectByOwnerUserId(userId);
        if (shop == null) {
            return WcwmResponse.error("您还没有店铺");
        }
        List<Product> list = productMapper.selectListByShopId(shop.getId());
        log.info("商家查询商品列表 shopId={}, size={}", shop.getId(), list.size());
        return WcwmResponse.success(list);
    }

    @GetMapping("/orders")
    public WcwmResponse<Map<String, Object>> listOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        Shop shop = shopMapper.selectByOwnerUserId(userId);
        if (shop == null) {
            return WcwmResponse.error("您还没有店铺");
        }
        Map<String, Object> data = orderService.listOrdersByShopId(shop.getId(), page, size);
        log.info("商家查询订单列表 shopId={}, page={}", shop.getId(), page);
        return WcwmResponse.success(data);
    }
}
