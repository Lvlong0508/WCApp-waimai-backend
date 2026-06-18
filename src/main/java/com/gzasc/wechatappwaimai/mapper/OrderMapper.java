package com.gzasc.wechatappwaimai.mapper;

import com.gzasc.wechatappwaimai.vo.OrderItemVO;
import com.gzasc.wechatappwaimai.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    List<OrderVO> selectOrderListByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);

    long countByUserId(@Param("userId") Long userId);

    List<OrderVO> selectOrderListByShopId(@Param("shopId") Long shopId, @Param("offset") int offset, @Param("size") int size);

    long countByShopId(@Param("shopId") Long shopId);

    List<OrderItemVO> selectItemsByOrderId(@Param("orderId") Long orderId);
}
