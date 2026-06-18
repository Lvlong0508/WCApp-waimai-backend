package com.gzasc.wechatappwaimai.mapper;

import com.gzasc.wechatappwaimai.entity.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMapper {

    List<Shop> selectOpenShops(@Param("offset") int offset, @Param("size") int size);

    long countOpenShops();

    Shop selectById(@Param("id") Long id);

    Shop selectByOwnerUserId(@Param("ownerUserId") Long ownerUserId);

    int updateLogo(@Param("id") Long id, @Param("logo") String logo);

    int insert(Shop shop);
}
