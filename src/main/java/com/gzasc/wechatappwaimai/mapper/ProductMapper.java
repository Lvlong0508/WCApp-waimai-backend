package com.gzasc.wechatappwaimai.mapper;

import com.gzasc.wechatappwaimai.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper {

    Product selectById(@Param("id") Long id);

    int updateImage(@Param("id") Long id, @Param("image") String image);
}
