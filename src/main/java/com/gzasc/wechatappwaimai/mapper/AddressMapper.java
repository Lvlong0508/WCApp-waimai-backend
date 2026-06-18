package com.gzasc.wechatappwaimai.mapper;

import com.gzasc.wechatappwaimai.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {

    Address selectDefaultByUserId(@Param("userId") Long userId);

    List<Address> selectByUserId(@Param("userId") Long userId);

    Address selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    int insert(Address address);

    int update(Address address);

    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    int clearDefaultByUserId(@Param("userId") Long userId);
}
