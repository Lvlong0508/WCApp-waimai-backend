package com.gzasc.wechatappwaimai.service;

import com.gzasc.wechatappwaimai.dto.ShopRegisterRequest;

import java.util.Map;

public interface ShopService {

    Map<String, Object> listShops(int page, int size);

    void registerShop(ShopRegisterRequest request, Long userId);
}
