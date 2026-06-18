package com.gzasc.wechatappwaimai.mapper;

import com.gzasc.wechatappwaimai.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    Product selectById(@Param("id") Long id);

    List<Product> selectListByShopId(@Param("shopId") Long shopId);

    int updateImage(@Param("id") Long id, @Param("image") String image);
}
