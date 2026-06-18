package com.gzasc.wechatappwaimai.controller.user;

import cn.dev33.satoken.stp.StpUtil;
import com.gzasc.wechatappwaimai.dto.ShopRegisterRequest;
import com.gzasc.wechatappwaimai.dto.WcwmResponse;
import com.gzasc.wechatappwaimai.entity.Shop;
import com.gzasc.wechatappwaimai.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserShopController {

    private final ShopService shopService;

    @GetMapping("/shops")
    public WcwmResponse<Map<String, Object>> listShops(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> data = shopService.listShops(page, size);
        log.info("用户请求商铺列表 page={}, size={}", page, size);
        return WcwmResponse.success(data);
    }

    @GetMapping("/merchant/my-shop")
    public WcwmResponse<Shop> getMyShop() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        Shop shop = shopService.getShopByOwnerUserId(userId);
        log.info("用户查询自己店铺 userId={}, hasShop={}", userId, shop != null);
        return WcwmResponse.success(shop);
    }

    @PostMapping("/merchant/register")
    public WcwmResponse<Void> register(@RequestBody ShopRegisterRequest request) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        shopService.registerShop(request, userId);
        log.info("用户注册商家 userId={}", userId);
        return WcwmResponse.success(null);
    }
}
