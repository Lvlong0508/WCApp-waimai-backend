package com.gzasc.wechatappwaimai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.gzasc.wechatappwaimai.dto.LoginRequest;
import com.gzasc.wechatappwaimai.entity.User;
import com.gzasc.wechatappwaimai.mapper.UserMapper;
import com.gzasc.wechatappwaimai.service.UserService;
import com.gzasc.wechatappwaimai.utils.WxApiUtil;
import com.gzasc.wechatappwaimai.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final WxApiUtil wxApiUtil;

    @Override
    public Map<String, Object> login(LoginRequest req) {
        if (req == null || req.getCode() == null || req.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("code不能为空");
        }
        String code = req.getCode().trim();

        Map<String, Object> wxResult = wxApiUtil.jscode2session(code);
        String openid = (String) wxResult.get("openid");
        String unionid = (String) wxResult.get("unionid");
        if (openid == null || openid.trim().isEmpty()) {
            throw new IllegalArgumentException("微信登录失败: openid为空");
        }

        String reqNickName = req.getNickName();
        String reqAvatarUrl = req.getAvatarUrl();
        boolean hasNick = reqNickName != null && !reqNickName.trim().isEmpty();
        boolean hasAvatar = reqAvatarUrl != null && !reqAvatarUrl.trim().isEmpty();

        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            LocalDateTime now = LocalDateTime.now();
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setNickName(hasNick ? reqNickName.trim() : "微信用户");
            user.setAvatarUrl(hasAvatar ? reqAvatarUrl.trim() : null);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            userMapper.insert(user);
        } else if (hasNick || hasAvatar) {
            userMapper.updateProfile(user.getId(),
                    hasNick ? reqNickName.trim() : null,
                    hasAvatar ? reqAvatarUrl.trim() : null);
            user = userMapper.selectById(user.getId());
        }

        StpUtil.login(user.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        result.put("accountType", "USER");
        result.put("userInfo", toUserVO(user));
        return result;
    }

    @Override
    public UserVO getCurrentUserInfo(Long userId) {
        return toUserVO(userMapper.selectById(userId));
    }

    private UserVO toUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setNickName(user.getNickName());
        userVO.setAvatarUrl(user.getAvatarUrl());
        userVO.setPhone(user.getPhone());
        userVO.setAccountType("USER");
        return userVO;
    }
}
