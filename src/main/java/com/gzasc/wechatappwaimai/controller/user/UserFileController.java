package com.gzasc.wechatappwaimai.controller.user;

import cn.dev33.satoken.stp.StpUtil;
import com.gzasc.wechatappwaimai.dto.WcwmResponse;
import com.gzasc.wechatappwaimai.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserFileController {

    private final FileService fileService;

    @PostMapping("/upload/user")
    public WcwmResponse<Map<String, String>> uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        String url = fileService.uploadUserAvatar(file, userId);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return WcwmResponse.success(data);
    }

    @PostMapping("/upload/product/{productId}")
    public WcwmResponse<Map<String, String>> uploadProductImage(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long productId) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        String url = fileService.uploadProductImage(file, productId, userId);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return WcwmResponse.success(data);
    }

    @PostMapping("/upload/shoplogo/{shopId}")
    public WcwmResponse<Map<String, String>> uploadShopLogo(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long shopId) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        String url = fileService.uploadShopLogo(file, shopId, userId);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return WcwmResponse.success(data);
    }
}
