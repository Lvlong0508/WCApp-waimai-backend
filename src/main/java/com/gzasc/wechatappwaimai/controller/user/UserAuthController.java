package com.gzasc.wechatappwaimai.controller.user;

import cn.dev33.satoken.stp.StpUtil;
import com.gzasc.wechatappwaimai.dto.LoginRequest;
import com.gzasc.wechatappwaimai.dto.WcwmResponse;
import com.gzasc.wechatappwaimai.service.UserService;
import com.gzasc.wechatappwaimai.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;

    @PostMapping("/login")
    public WcwmResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        if (request == null || request.getCode() == null || request.getCode().trim().isEmpty()) {
            return WcwmResponse.error(400, "参数code不能为空");
        }
        Map<String, Object> data = userService.login(request);
        return WcwmResponse.success("登录成功", data);
    }

    @GetMapping("/info")
    public WcwmResponse<UserVO> info() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        UserVO data = userService.getCurrentUserInfo(userId);
        return WcwmResponse.success(data);
    }
}
