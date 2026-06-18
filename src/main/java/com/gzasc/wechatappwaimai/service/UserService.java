package com.gzasc.wechatappwaimai.service;

import com.gzasc.wechatappwaimai.dto.LoginRequest;
import com.gzasc.wechatappwaimai.vo.UserVO;

import java.util.Map;

public interface UserService {

    Map<String, Object> login(LoginRequest req);

    UserVO getCurrentUserInfo(Long userId);
}
