package com.gzasc.wechatappwaimai.service;

import com.gzasc.wechatappwaimai.dto.ShopRegisterRequest;
import com.gzasc.wechatappwaimai.entity.Shop;

import java.util.Map;

public interface ShopService {

    Map<String, Object> listShops(int page, int size);

    void registerShop(ShopRegisterRequest request, Long userId);

    Shop getShopByOwnerUserId(Long userId);
}
