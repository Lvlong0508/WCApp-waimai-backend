package com.gzasc.wechatappwaimai.mapper;

import com.gzasc.wechatappwaimai.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User selectByOpenid(@Param("openid") String openid);

    User selectById(@Param("id") Long id);

    int updateAvatarUrl(@Param("id") Long id, @Param("avatarUrl") String avatarUrl);

    int updateProfile(@Param("id") Long id,
                      @Param("nickName") String nickName,
                      @Param("avatarUrl") String avatarUrl);

    int insert(User user);
}
