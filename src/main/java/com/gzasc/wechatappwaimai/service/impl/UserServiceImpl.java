package com.gzasc.wechatappwaimai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.gzasc.wechatappwaimai.dto.LoginRequest;
import com.gzasc.wechatappwaimai.entity.User;
import com.gzasc.wechatappwaimai.mapper.UserMapper;
import com.gzasc.wechatappwaimai.service.FileService;
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
    private final FileService fileService;

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
            // 新用户 → 注册
            LocalDateTime now = LocalDateTime.now();
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setNickName(hasNick ? reqNickName.trim() : "微信用户");
            user.setAvatarUrl(hasAvatar ? reqAvatarUrl.trim() : null);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            userMapper.insert(user);

            // 迁移临时头像到永久目录
            if (hasAvatar) {
                String permanentUrl = fileService.moveTempAvatarToPermanent(reqAvatarUrl.trim(), openid);
                if (!permanentUrl.equals(reqAvatarUrl.trim())) {
                    userMapper.updateProfile(user.getId(), null, permanentUrl);
                    user.setAvatarUrl(permanentUrl);
                }
            }
        } else {
            // 老用户 → 登录，按需更新资料
            boolean needUpdate = false;
            String newNick = null;
            String newAvatar = null;

            if (hasNick && (user.getNickName() == null || user.getNickName().isBlank())) {
                newNick = reqNickName.trim();
                needUpdate = true;
            }
            if (hasAvatar && (user.getAvatarUrl() == null || user.getAvatarUrl().isBlank())) {
                String permanentUrl = fileService.moveTempAvatarToPermanent(reqAvatarUrl.trim(), openid);
                if (!permanentUrl.equals(reqAvatarUrl.trim())) {
                    newAvatar = permanentUrl;
                    needUpdate = true;
                }
            }

            if (needUpdate) {
                userMapper.updateProfile(user.getId(), newNick, newAvatar);
                user = userMapper.selectById(user.getId());
            }

            // 清理未使用的临时文件
            if (hasAvatar && user.getAvatarUrl() != null && !user.getAvatarUrl().equals(reqAvatarUrl.trim())) {
                fileService.deleteTempAvatarByUrl(reqAvatarUrl.trim());
            }
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
