package com.gzasc.wechatappwaimai.service;

import java.util.Map;

public interface OrderService {

    Map<String, Object> listOrders(Long userId, int page, int size);
}
