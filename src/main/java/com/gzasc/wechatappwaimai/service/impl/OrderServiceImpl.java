package com.gzasc.wechatappwaimai.service.impl;

import com.gzasc.wechatappwaimai.mapper.OrderMapper;
import com.gzasc.wechatappwaimai.service.OrderService;
import com.gzasc.wechatappwaimai.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    public Map<String, Object> listOrders(Long userId, int page, int size) {
        int currentPage = page <= 0 ? 1 : page;
        int pageSize = size <= 0 ? 20 : size;
        int offset = (currentPage - 1) * pageSize;

        long total = orderMapper.countByUserId(userId);
        List<OrderVO> list = orderMapper.selectOrderListByUserId(userId, offset, pageSize);
        for (OrderVO orderVO : list) {
            orderVO.setOrderItemList(orderMapper.selectItemsByOrderId(orderVO.getId()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    @Override
    public Map<String, Object> listOrdersByShopId(Long shopId, int page, int size) {
        int currentPage = page <= 0 ? 1 : page;
        int pageSize = size <= 0 ? 20 : size;
        int offset = (currentPage - 1) * pageSize;

        long total = orderMapper.countByShopId(shopId);
        List<OrderVO> list = orderMapper.selectOrderListByShopId(shopId, offset, pageSize);
        for (OrderVO orderVO : list) {
            orderVO.setOrderItemList(orderMapper.selectItemsByOrderId(orderVO.getId()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }
}
